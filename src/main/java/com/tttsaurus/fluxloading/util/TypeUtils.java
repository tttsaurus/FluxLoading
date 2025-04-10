package com.tttsaurus.fluxloading.util;

public final class TypeUtils {

    public static boolean isIntOrWrappedInt(Class<?> clazz) {
        return clazz.getName()
            .equals("int") || clazz.equals(Integer.class);
    }

    public static boolean isLongOrWrappedLong(Class<?> clazz) {
        return clazz.getName()
            .equals("long") || clazz.equals(Long.class);
    }

    public static boolean isDoubleOrWrappedDouble(Class<?> clazz) {
        return clazz.getName()
            .equals("double") || clazz.equals(Double.class);
    }

    public static boolean isFloatOrWrappedFloat(Class<?> clazz) {
        return clazz.getName()
            .equals("float") || clazz.equals(Float.class);
    }

    public static boolean isBooleanOrWrappedBoolean(Class<?> clazz) {
        return clazz.getName()
            .equals("boolean") || clazz.equals(Boolean.class);
    }
}
