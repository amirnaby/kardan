package com.niam.kardan.model.basedata;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niam.kardan.util.BaseDataSerializer;
import com.niam.usermanagement.model.entities.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Setter
@Getter
@MappedSuperclass
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonSerialize(using = BaseDataSerializer.class)
public abstract class BaseData extends Auditable {
    @Column(nullable = false)
    private String name;

    private String description;

    @Column(unique = true, nullable = false, updatable = false)
    private String code;

    public BaseData() {
    }

    public BaseData(Long id, String name, String description, String code) {
        this.name = name;
        this.description = description;
        this.code = code;
    }

    public BaseData(String code) {
        this.code = code;
    }

    public static <T extends BaseData> T ofCode(Class<T> type, String code) {
        try {
            T instance = type.getDeclaredConstructor().newInstance();
            instance.setCode(code);
            return instance;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot instantiate " + type.getSimpleName(), e);
        }
    }
}