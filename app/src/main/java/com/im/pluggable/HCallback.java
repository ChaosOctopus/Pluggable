package com.im.pluggable;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

/**
 * Created by zhaoyuanchao on 2018/12/6  20:13
 * Hook H.Callback 用于将启动的targetActivity替换占坑的StubActivity。
 */
public class HCallback implements Handler.Callback {
    public static final int LAINCH_ACTIVITY = 100;
    Handler mHandler;

    public HCallback(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == LAINCH_ACTIVITY){
            Object r = msg.obj;
            try{
                Intent intent = (Intent) FieldUtil.getField(r.getClass(),r,"intent");
                Intent target = intent.getParcelableExtra(HookHelper.TARGET_INTENT);
                intent.setComponent(target.getComponent());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        mHandler.handleMessage(msg);
        return true;
    }
}
