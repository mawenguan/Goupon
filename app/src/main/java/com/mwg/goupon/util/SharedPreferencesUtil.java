package com.mwg.goupon.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.mwg.goupon.constant.Constant;

/**
 * 对偏好设置文件惊醒操作
 * 1、Context下的getSharedPreferences(文件名, Context.MODE模式);
 * 2、Activity的getPreferences(模式)；
 * 3、PreferenceManager的getDefaultSharedPreferences(Context）;
 * Created by mwg on 2018/1/11.
 */

public class SharedPreferencesUtil {

    SharedPreferences sp;

    //通过构造器重载，以不同的方式获得偏好设置文件
    public SharedPreferencesUtil(Context context,String name) {
        sp = context.getSharedPreferences(name,Context.MODE_PRIVATE);
    }

    public SharedPreferencesUtil(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Boolean isFirst(){
        return sp.getBoolean(Constant.FIRST,true);
    }

    public void setFirst(Boolean flag){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constant.FIRST,flag);
        editor.commit();
    }
}

