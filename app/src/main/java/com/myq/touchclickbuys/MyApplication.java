package com.myq.touchclickbuys;

import android.app.Application;
import android.content.Context;

import gr.free.grfastuitils.GrUtilsInstance;

/**
 * Create by guorui on 2020/5/22
 * Last update 2020/5/22
 * Description:
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;
        GrUtilsInstance.getmContext(getInstance());

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static MyApplication getInstance() {
        return instance;
    }


}

