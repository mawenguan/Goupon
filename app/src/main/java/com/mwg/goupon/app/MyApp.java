package com.mwg.goupon.app;

import android.app.Application;

import com.mwg.goupon.bean.CityNameBean;

import java.util.List;

/**
 * Created by mwg on 2018/1/15.
 */

public class MyApp extends Application {

    public static MyApp CONTEXT;

    //城市名称的缓存
    public static List<CityNameBean> cityNameBeanList ;

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT=this;
    }
}
