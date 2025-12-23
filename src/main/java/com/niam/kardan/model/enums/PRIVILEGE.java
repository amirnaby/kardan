package com.niam.kardan.model.enums;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public final class PRIVILEGE {
    private PRIVILEGE() {
    }

    public static final String APP_MANAGE = "APP_MANAGE";
    public static final String MACHINE_MANAGE = "MACHINE_MANAGE";
    public static final String MACHINE_VIEW = "MACHINE_VIEW";
    public static final String OPERATION_MANAGE = "OPERATION_MANAGE";
    public static final String OPERATION_EXECUTION = "OPERATION_EXECUTION";
    public static final String SHIFT_MANAGE = "SHIFT_MANAGE";
    public static final String PART_MANAGE = "PART_MANAGE";
    public static final String PART_VIEW = "PART_VIEW";
    public static final String PROJECT_MANAGE = "PROJECT_MANAGE";
    public static final String PROJECT_VIEW = "PROJECT_VIEW";

    public static String[] values() {
        return Arrays.stream(PRIVILEGE.class.getDeclaredFields())
                .filter(field ->
                        Modifier.isStatic(field.getModifiers()) &&
                                Modifier.isFinal(field.getModifiers()) &&
                                field.getType().equals(String.class)
                )
                .map(field -> {
                    try {
                        return (String) field.get(null);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Cannot access privilege field: " + field.getName(), e);
                    }
                })
                .toArray(String[]::new);
    }
}