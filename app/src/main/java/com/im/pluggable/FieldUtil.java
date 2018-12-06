package com.im.pluggable;

import java.lang.reflect.Field;

/**
 * Created by zhaoyuanchao on 2018/12/6  17:13
 * Hook方式来反射Hook点的工具类
 */
public class FieldUtil {

    public static Object getField(Class clazz,Object target,String name)throws Exception{
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    public static Field getField(Class clazz,String name) throws Exception{
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    public static void setField(Class clazz,Object target,String name,Object value) throws Exception{
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target,value);
    }
}
