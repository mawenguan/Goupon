package com.mwg.goupon.util;

import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mwg.goupon.app.MyApp;
import com.mwg.goupon.ui.HttpUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mwg on 2018/1/15.
 */

public class VolleyClient {

    //1、声明一个私有的静态的属性
    private static VolleyClient INSTANCE = new VolleyClient();

    //2、声明一个公有的静态的获取1属性的方法
    public static VolleyClient getInstance() {
        if (INSTANCE == null) {
            synchronized (VolleyClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new VolleyClient();
                }
            }
        }
        return INSTANCE;
    }

    RequestQueue queue;

    //3、构造器私有化
    private VolleyClient() {
        queue = Volley.newRequestQueue(MyApp.CONTEXT);
    }
    private VolleyClient(Context context) {
        queue = Volley.newRequestQueue(MyApp.CONTEXT);
    }

    public void test() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("city", "北京");
        params.put("category", "美食");
        String url = HttpUtil.getURL("http://api.dianping.com/v1/business/find_businesses", params);

        Request request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.i("TAG:", "利用Volley获取的服务器响应内容" + s);
            }
        }, null);

        //将请求对象放到请求队列中
        queue.add(request);
    }
}