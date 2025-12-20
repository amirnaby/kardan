package com.niam.kardan.util;

import com.niam.kardan.model.basedata.BaseData;
import org.hibernate.Hibernate;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.lang.reflect.Field;
import org.springframework.util.ReflectionUtils;

public class BaseDataSerializer extends JsonSerializer<BaseData> {

    @Override
    public void serialize(BaseData value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        BaseData real = (BaseData) Hibernate.unproxy(value);

        gen.writeStartObject();
        gen.writeNumberField("id", getId(real));
        gen.writeStringField("code", real.getCode());
        gen.writeStringField("name", real.getName());
        gen.writeStringField("description", real.getDescription());
        gen.writeEndObject();
    }

    private Long getId(BaseData baseData) {
        try {
            Class<?> realClass = Hibernate.getClass(baseData);
            Field field = ReflectionUtils.findField(realClass, "id");
            if (field == null) return null;
            field.setAccessible(true);
            return (Long) field.get(baseData);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read BaseData id", e);
        }
    }
}