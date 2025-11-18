package com.niam.kardan.service;

import com.niam.kardan.model.basedata.BaseData;
import com.niam.kardan.util.EntityClassResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenericBaseDataServiceFactory {
    private final EntityClassResolver resolver;
    private final ApplicationContext context;

    public <T extends BaseData> BaseDataServiceProxy<T> create(Class<T> type) {
        BaseDataServiceProxy<T> proxy = context.getBean(BaseDataServiceProxy.class);
        proxy.setType(type);
        return proxy;
    }

    public <T extends BaseData> BaseDataServiceProxy<T> create(String simpleEntityName) {
        Class<? extends BaseData> type = resolver.resolve(simpleEntityName);
        return (BaseDataServiceProxy<T>) create(type);
    }
}