package com.espressif.iot.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by billmogen on 2016/11/22.
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }
    public static Context getContext() {
        return context;
    }
}
