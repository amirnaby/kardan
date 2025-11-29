package com.niam.kardan.service;

import com.niam.kardan.model.basedata.BaseData;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Scope("prototype")
@RequiredArgsConstructor
public class BaseDataServiceProxy<T extends BaseData> {
    private final EntityManager em;
    private final CacheManager cacheManager;
    @Setter
    private Class<T> type;

    private GenericBaseDataService<T> delegate() {
        return new GenericBaseDataService<>(type, em, cacheManager);
    }

    @Transactional("transactionManager")
    public T create(BaseData p) {
        return delegate().create(p);
    }

    @Transactional("transactionManager")
    public T update(Long id, BaseData p) {
        return delegate().update(id, p);
    }

    @Transactional("transactionManager")
    public void delete(Long id) {
        delegate().delete(id);
    }

    @Transactional(readOnly = true, value = "transactionManager")
    public List<T> getAll() {
        return delegate().getAll();
    }

    @Transactional(readOnly = true, value = "transactionManager")
    public T getById(Long id) {
        return delegate().getById(id);
    }

    @Transactional(readOnly = true, value = "transactionManager")
    public T getByCode(String code) {
        return delegate().getByCode(code);
    }
}