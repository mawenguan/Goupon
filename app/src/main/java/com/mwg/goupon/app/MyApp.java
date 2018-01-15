package com.mwg.goupon.app;

import android.app.Application;

/**
 * Created by mwg on 2018/1/15.
 */

public class MyApp extends Application {

    public static MyApp CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT=this;
    }
}
