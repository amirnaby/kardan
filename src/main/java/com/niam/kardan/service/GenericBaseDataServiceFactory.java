package com.niam.kardan.service;

import com.niam.kardan.model.basedata.BaseData;
import com.niam.kardan.util.EntityClassResolver;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenericBaseDataServiceFactory {
    private final EntityManager em;
    private final CacheManager cacheManager;
    private final EntityClassResolver resolver;

    public <T extends BaseData> GenericBaseDataService<T> create(Class<T> type) {
        return new GenericBaseDataService<>(type, em, cacheManager);
    }

    public <T extends BaseData> GenericBaseDataService<T> create(String simpleEntityName) {
        Class<? extends BaseData> type = resolver.resolve(simpleEntityName);
        return (GenericBaseDataService<T>) create(type);
    }
}