package com.im.pluggable;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhaoyuanchao on 2018/12/6  18:31
 */
public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try{
            //用占坑activity 为了通过ASM校验
            HookHelper.hookAMS();
            //将占坑Ac替换为targetAc
//            HookHelper.hookHandler();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
