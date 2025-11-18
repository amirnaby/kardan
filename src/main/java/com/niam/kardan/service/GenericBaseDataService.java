package com.niam.kardan.service;

import com.niam.common.exception.EntityNotFoundException;
import com.niam.common.exception.ResultResponseStatus;
import com.niam.kardan.model.basedata.BaseData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class GenericBaseDataService<T extends BaseData> {
    private final EntityManager em;
    private final CacheManager cacheManager;

    @Getter
    private final Class<T> type;
    private final String entityName;

    public GenericBaseDataService(Class<T> type, EntityManager em, CacheManager cacheManager) {
        this.type = type;
        this.entityName = type.getSimpleName();
        this.em = em;
        this.cacheManager = cacheManager;
    }

    private String cacheAllName() {
        return "basedata::" + entityName + "::all";
    }

    private String cacheByIdName() {
        return "basedata::" + entityName + "::id";
    }

    private String cacheByCodeName() {
        return "basedata::" + entityName + "::code";
    }

    public List<T> getAll() {
        Cache c = cacheManager.getCache(cacheAllName());
        if (c != null) {
            @SuppressWarnings("unchecked")
            List<T> cached = c.get("all", List.class);
            if (cached != null) return cached;
        }
        TypedQuery<T> q = em.createQuery("SELECT e FROM " + entityName + " e", type);
        List<T> list = q.getResultList();
        if (c != null) c.put("all", list);
        return list;
    }

    public T getById(Long id) {
        Cache c = cacheManager.getCache(cacheByIdName());
        if (c != null) {
            T cached = c.get(id, type);
            if (cached != null) return cached;
        }
        T found = em.find(type, id);
        if (found == null) throw notFound(id);
        if (c != null) c.put(id, found);
        return found;
    }

    public Optional<T> findByCodeOptional(String code) {
        if (!StringUtils.hasText(code)) return Optional.empty();
        Cache c = cacheManager.getCache(cacheByCodeName());
        if (c != null) {
            T cached = c.get(code, type);
            if (cached != null) return Optional.of(cached);
        }
        TypedQuery<T> q = em.createQuery("SELECT e FROM " + entityName + " e WHERE e.code = :code", type);
        q.setParameter("code", code);
        List<T> list = q.setMaxResults(1).getResultList();
        if (list.isEmpty()) return Optional.empty();
        T bd = list.getFirst();
        if (c != null) c.put(code, bd);
        return Optional.of(bd);
    }

    public T getByCode(String code) {
        return findByCodeOptional(code).orElseThrow(() ->
                new EntityNotFoundException(
                        ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                        ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                        entityName + " with code=" + code + " not found"));
    }

    public T create(Map<String, Object> payload) {
        try {
            T instance = type.getDeclaredConstructor().newInstance();
            if (payload.containsKey("name")) instance.setName(String.valueOf(payload.get("name")));
            if (payload.containsKey("description")) instance.setDescription(String.valueOf(payload.get("description")));
            if (payload.containsKey("code")) instance.setCode(String.valueOf(payload.get("code")));
            em.persist(instance);
            evictCaches();
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create " + entityName, e);
        }
    }

    public T update(Long id, Map<String, Object> payload) {
        T existing = em.find(type, id);
        if (existing == null) throw notFound(id);
        if (payload.containsKey("name")) existing.setName(String.valueOf(payload.get("name")));
        if (payload.containsKey("description")) existing.setDescription(String.valueOf(payload.get("description")));
        em.merge(existing);
        evictCaches();
        return existing;
    }

    public void delete(Long id) {
        T existing = em.find(type, id);
        if (existing == null) throw notFound(id);
        em.remove(existing);
        evictCaches();
    }

    private void evictCaches() {
        Cache c;
        c = cacheManager.getCache(cacheAllName());
        if (c != null) c.clear();
        c = cacheManager.getCache(cacheByIdName());
        if (c != null) c.clear();
        c = cacheManager.getCache(cacheByCodeName());
        if (c != null) c.clear();
    }

    private EntityNotFoundException notFound(Long id) {
        return new EntityNotFoundException(
                ResultResponseStatus.ENTITY_NOT_FOUND.getResponseCode(),
                ResultResponseStatus.ENTITY_NOT_FOUND.getReasonCode(),
                entityName + " with id=" + id + " not found");
    }
}