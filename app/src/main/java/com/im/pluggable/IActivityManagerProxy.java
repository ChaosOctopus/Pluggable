package com.im.pluggable;

import android.content.Intent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by zhaoyuanchao on 2018/12/6  17:20
 * Hook启动Activity的hook点 选择为IActivityManager
 * IActivityManager的代理类，这么做的目的就是避开AMS的检测
 */
public class IActivityManagerProxy implements InvocationHandler {
    private Object mActivityManager;
    public static final String TAG = "IActivityManagerProxy";

    public IActivityManagerProxy(Object mActivityManager) {
        this.mActivityManager = mActivityManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //拦截startActivity方法
        if ("startActivity".equals(method.getName())){
            Intent intent = null;
            int index = 0;
            for (int i=0; i<args.length;i++){
                if (args[i] instanceof Intent){
                    index = i;
                    break;
                }
            }
            //获取到原本要启动的TargetActivity的Intent
            intent = (Intent) args[index];
            //定义一个本地占坑的启动StubActivity的Intent
            Intent subIntent = new Intent();

            String packageName = "com.im.pluggable";
            subIntent.setClassName(packageName,packageName+".StubActivity");
            //将TargetActivity的Intent保存到subIntent，为了以后还原TargetActivity
            subIntent.putExtra("target_intent",intent);
            //用subIntent的值赋值给参数args，这样启动目标就变为了StubActivity
            args[index] = subIntent;
        }
        return method.invoke(mActivityManager,args);
    }
}
