package com.mwg.goupon.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.mwg.goupon.R;
import com.mwg.goupon.constant.Constant;
import com.mwg.goupon.util.SharedPreferencesUtil;

public class SplashActivity extends Activity {

    SharedPreferencesUtil sharedPreferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //读取偏好设置中的值
//                SharedPreferences sp = getSharedPreferences(
//                        "sp", Context.MODE_PRIVATE);
//                boolean first = sp.getBoolean(Constant.FIRST, true);
//                //根据是否是第一次使用实现页面的跳转
                Intent intent;
                if (sharedPreferencesUtil.isFirst()){
                    intent = new Intent(SplashActivity.this,
                            GuideActivity.class);
//                    SharedPreferences.Editor edit = sp.edit();
//                    edit.putBoolean(Constant.FIRST,false);
//                    edit.commit();
                    sharedPreferencesUtil.setFirst(false);
                }else {
                    intent = new Intent(SplashActivity.this,
                            MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        },1500);
    }
}
