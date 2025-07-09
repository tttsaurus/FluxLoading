package com.tttsaurus.fluxloading.util;

import com.tttsaurus.fluxloading.function.*;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class AccessorUnreflector
{
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    //<editor-fold desc="find field">
    @Nullable
    private static Field findDeclaredField(Class<?> clazz, String fieldName, String obfFieldName)
    {
        Field field = null;
        try
        {
            field = clazz.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException e)
        {
            try
            {
                field = clazz.getDeclaredField(obfFieldName);
            }
            catch (NoSuchFieldException ignored) { }
        }
        return field;
    }
    @Nullable
    private static Field findDeclaredField(Class<?> clazz, String fieldName)
    {
        Field field = null;
        try
        {
            field = clazz.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException ignored) { }
        return field;
    }
    @Nullable
    private static Field findField(Class<?> clazz, String fieldName, String obfFieldName)
    {
        Field field = null;
        try
        {
            field = clazz.getField(fieldName);
        }
        catch (NoSuchFieldException e)
        {
            try
            {
                field = clazz.getField(obfFieldName);
            }
            catch (NoSuchFieldException ignored) { }
        }
        return field;
    }
    @Nullable
    private static Field findField(Class<?> clazz, String fieldName)
    {
        Field field = null;
        try
        {
            field = clazz.getField(fieldName);
        }
        catch (NoSuchFieldException ignored) { }
        return field;
    }
    //</editor-fold>

    //<editor-fold desc="unreflect">
    private static FuncBase unreflectGetter(Field field)
    {
        if (field.getType().isPrimitive())
            return unreflectGetterHelper(field, TypeUtils.toWrappedPrimitive(field.getType()), field.getDeclaringClass());
        else
            return unreflectGetterHelper(field, field.getType(), field.getDeclaringClass());
    }
    @SuppressWarnings("all")
    @Nullable
    private static <TReturn, TOwner> FuncBase unreflectGetterHelper(Field field, Class<TReturn> clazz1, Class<TOwner> clazz2)
    {
        field.setAccessible(true);
        FuncBase getter = null;
        try
        {
            MethodHandle handle = lookup.unreflectGetter(field);
            if (Modifier.isStatic(field.getModifiers()))
            {
                getter = new Func<TReturn>()
                {
                    @Override
                    public TReturn invoke()
                    {
                        try
                        {
                            return (TReturn)handle.invoke();
                        }
                        catch (Throwable e) { return null; }
                    }
                };
            }
            else
            {
                getter = new Func_1Param<TReturn, TOwner>()
                {
                    @Override
                    public TReturn invoke(TOwner arg0)
                    {
                        try
                        {
                            return (TReturn)handle.invoke(arg0);
                        }
                        catch (Throwable e) { return null; }
                    }
                };
            }
        }
        catch (Exception ignored) {}

        return getter;
    }

    private static ActionBase unreflectSetter(Field field)
    {
        if (field.getType().isPrimitive())
            return unreflectSetterHelper(field, TypeUtils.toWrappedPrimitive(field.getType()), field.getDeclaringClass());
        else
            return unreflectSetterHelper(field, field.getType(), field.getDeclaringClass());
    }
    @SuppressWarnings("all")
    @Nullable
    private static <TField, TOwner> ActionBase unreflectSetterHelper(Field field, Class<TField> clazz1, Class<TOwner> clazz2)
    {
        field.setAccessible(true);
        ActionBase setter = null;
        try
        {
            MethodHandle handle = lookup.unreflectSetter(field);
            if (Modifier.isStatic(field.getModifiers()))
            {
                setter = new Action_1Param<TField>()
                {
                    @Override
                    public void invoke(TField arg0)
                    {
                        try
                        {
                            handle.invoke(arg0);
                        }
                        catch (Throwable e) { }
                    }
                };
            }
            else
            {
                setter = new Action_2Param<TOwner, TField>()
                {
                    @Override
                    public void invoke(TOwner arg0, TField arg1)
                    {
                        try
                        {
                            handle.invoke(arg0, arg1);
                        }
                        catch (Throwable e) { }
                    }
                };
            }
        }
        catch (Exception ignored) {}

        return setter;
    }
    //</editor-fold>

    // return Func or Func_1Param depends on static modifier
    //<editor-fold desc="get getter">
    @Nullable
    public static FuncBase getDeclaredFieldGetter(Class<?> clazz, String fieldName, String obfFieldName)
    {
        Field field = findDeclaredField(clazz, fieldName, obfFieldName);
        if (field == null)
            return null;
        return unreflectGetter(field);
    }
    @Nullable
    public static FuncBase getDeclaredFieldGetter(Class<?> clazz, String fieldName)
    {
        Field field = findDeclaredField(clazz, fieldName);
        if (field == null)
            return null;
        return unreflectGetter(field);
    }
    @Nullable
    public static FuncBase getFieldGetter(Class<?> clazz, String fieldName, String obfFieldName)
    {
        Field field = findField(clazz, fieldName, obfFieldName);
        if (field == null)
            return null;
        return unreflectGetter(field);
    }
    @Nullable
    public static FuncBase getFieldGetter(Class<?> clazz, String fieldName)
    {
        Field field = findField(clazz, fieldName);
        if (field == null)
            return null;
        return unreflectGetter(field);
    }
    //</editor-fold>

    // return Action_1Param or Action_2Param depends on static modifier
    //<editor-fold desc="get setter">
    @Nullable
    public static ActionBase getDeclaredFieldSetter(Class<?> clazz, String fieldName, String obfFieldName)
    {
        Field field = findDeclaredField(clazz, fieldName, obfFieldName);
        if (field == null)
            return null;
        return unreflectSetter(field);
    }
    @Nullable
    public static ActionBase getDeclaredFieldSetter(Class<?> clazz, String fieldName)
    {
        Field field = findDeclaredField(clazz, fieldName);
        if (field == null)
            return null;
        return unreflectSetter(field);
    }
    @Nullable
    public static ActionBase getFieldSetter(Class<?> clazz, String fieldName, String obfFieldName)
    {
        Field field = findField(clazz, fieldName, obfFieldName);
        if (field == null)
            return null;
        return unreflectSetter(field);
    }
    @Nullable
    public static ActionBase getFieldSetter(Class<?> clazz, String fieldName)
    {
        Field field = findField(clazz, fieldName);
        if (field == null)
            return null;
        return unreflectSetter(field);
    }
    //</editor-fold>
}
