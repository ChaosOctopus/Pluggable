package com.im.pluggable;

import android.os.Build;
import android.os.Handler;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Created by zhaoyuanchao on 2018/12/6  18:18
 */
public class HookHelper {
    public static final String TARGET_INTENT = "target_intent";

    public static void hookAMS() throws Exception {
        Object defaultSingleton = null;
        if (Build.VERSION.SDK_INT >= 26){
            Class<?> activityManagerClazz = Class.forName("android.app.ActivityManager");
            defaultSingleton = FieldUtil.getField(activityManagerClazz,null,"IActivityManagerSingleton");
        }else{
            Class<?> activityManagerNativeClazz = Class.forName("android.app.ActivityManagerNative");
            defaultSingleton = FieldUtil.getField(activityManagerNativeClazz,null,"gDefault");
        }
        Class<?> singletonClazz = Class.forName("android.util.Singleton");
        Field mInstanceField = FieldUtil.getField(singletonClazz,"mInstance");
        //获取IActivityManager
        Object iActivityManager = mInstanceField.get(defaultSingleton);
        Class<?> iActivityManagerClazz = Class.forName("android.app.IActivityManager");
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{iActivityManagerClazz},new IActivityManagerProxy(iActivityManager));
        mInstanceField.set(defaultSingleton,proxy);
    }

    public static void hookHandler() throws Exception{
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Object currentActivityThread = FieldUtil.getField(activityThreadClass,null,"sCurrentActivityThread");
        Field mHField = FieldUtil.getField(activityThreadClass,"mH");
        Handler mH = (Handler) mHField.get(currentActivityThread);
        FieldUtil.setField(Handler.class,mH,"mCallback",new HCallback(mH));
    }
}
