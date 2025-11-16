package com.niam.kardan.init;

import com.niam.kardan.model.basedata.BaseData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

@Profile("init")
@Slf4j
@Component
@RequiredArgsConstructor
public class BaseDataInitializer {
    private final EntityManager em;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional("transactionManager")
    public void init() {
        log.info("BaseDataInitializer start");
        Reflections reflections = new Reflections("com.niam.kardan.model.basedata.enums");
        Set<Class<? extends Enum>> enumClasses = reflections.getSubTypesOf(Enum.class);

        for (Class<? extends Enum> enumClass : enumClasses) {
            try {
                Field aClassField = enumClass.getDeclaredField("aClass");
                aClassField.setAccessible(true);
                @SuppressWarnings("unchecked")
                Class<? extends BaseData> entityClass = (Class<? extends BaseData>) aClassField.get(null);
                String entityName = entityClass.getSimpleName();

                log.info("Processing enum {} → entity {}", enumClass.getSimpleName(), entityName);

                Arrays.stream(enumClass.getEnumConstants()).forEach(ev -> {
                    String code = ev.name();
                    TypedQuery<Long> q = em.createQuery(
                            "SELECT COUNT(e) FROM " + entityName + " e WHERE e.code = :code", Long.class);
                    Long count = q.setParameter("code", code).getSingleResult();

                    if (count != null && count > 0) {
                        return;
                    }

                    try {
                        BaseData inst = BaseData.ofCode(entityClass, code);
                        inst.setName(ev.toString());
                        inst.setDescription(ev.toString());
                        em.persist(inst);
                        log.info("Inserted base data {} -> {}", entityName, code);
                    } catch (Exception ex) {
                        log.error("Failed to insert {} for {}", code, entityName, ex);
                    }
                });
            } catch (NoSuchFieldException e) {
                log.warn("Enum {} does not define static field aClass, skipping", enumClass.getSimpleName());
            } catch (Exception ex) {
                log.error("Failed to process enum {}", enumClass.getSimpleName(), ex);
            }
        }

        log.info("BaseDataInitializer finished");
    }
}