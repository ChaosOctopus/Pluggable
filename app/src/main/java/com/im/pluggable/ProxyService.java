package com.im.pluggable;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by zhaoyuanchao on 2018/12/7  15:14
 */
public class ProxyService extends Service {
    public static final String TARGET_SERVICE = "target_service";
    public static final String TAG = "ProxyService";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: ");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: " );
        if (null == intent || !intent.hasExtra(TARGET_SERVICE)){
            return START_STICKY;
        }
        String serviceName = intent.getStringExtra(TARGET_SERVICE);
        if (null == serviceName){
            return START_STICKY;
        }
        Service targetService = null;
        try{
            Class activityThreadClazz = Class.forName("android.app.ActivityThread");
            Method getActivityThreadMethod = activityThreadClazz.getDeclaredMethod("getApplicationThread");
            getActivityThreadMethod.setAccessible(true);
            Object activityThread = FieldUtil.getField(activityThreadClazz,null,"sCurrentActivityThread");
            Object applicationThread = getActivityThreadMethod.invoke(activityThread);

            Class iInterfaceClazz = Class.forName("android.os.IInterface");
            Method asBinderMethod = iInterfaceClazz.getDeclaredMethod("asBinder");
            asBinderMethod.setAccessible(true);
            Object token = asBinderMethod.invoke(applicationThread);
            Class serviceClazz = Class.forName("android.app.Service");
            Method attachMethod = serviceClazz.getDeclaredMethod("attach",Context.class,activityThreadClazz,
                    String.class,IBinder.class,Application.class,Object.class);
            attachMethod.setAccessible(true);
            Object defaultSingleton = null;
            if (Build.VERSION.SDK_INT >= 26){
                Class<?> activityManageClazz = Class.forName("android.app.ActivityManager");
                defaultSingleton = FieldUtil.getField(activityManageClazz,null,"IActivityManagerSingleton");
            }else{
                Class<?> activityManagerNativeClazz = Class.forName("android.app.ActivityManagerNative");
                defaultSingleton = FieldUtil.getField(activityManagerNativeClazz,null,"gDefault");
            }
            Class<?> singletonClazz = Class.forName("android.util.Singleton");
            Field mInstanceField = FieldUtil.getField(singletonClazz,"mInstance");
            Object iActivityManager = mInstanceField.get(defaultSingleton);
            targetService = (Service) Class.forName(serviceName).newInstance();
            attachMethod.invoke(targetService,this,activityThread,intent.getComponent().getClassName(),token,getApplication(),iActivityManager);
            targetService.onCreate();

        }catch (Exception e){
            e.printStackTrace();
            return START_STICKY;
        }
        targetService.onStartCommand(intent,flags,startId);
        return START_STICKY;

    }
}
