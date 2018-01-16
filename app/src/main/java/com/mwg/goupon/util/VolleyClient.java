package com.mwg.goupon.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mwg.goupon.app.MyApp;
import com.mwg.goupon.ui.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
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

    public void getDailyDeals(String city, final Response.Listener<String> listener) {
        //1、获取新增团购的ID列表
        final Map<String, String> params1 = new HashMap<String, String>();
        params1.put("city", city);
        params1.put("date", new SimpleDateFormat("yyyy-MM-dd").
                format(System.currentTimeMillis()));
        final String url1 = HttpUtil.getURL("http://api.dianping.com/v1/deal/get_daily_new_id_list", params1);
        StringRequest request1 = new StringRequest(url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                        /*
                        {
                              "status": "OK",
                              "count": 309,
                              "id_list": [
                                "1-33946",
                                "1-4531",
                                "1-4571",
                                "1-5336",
                                "1-5353",
                                "......"  ]  }
                         */

                //利用Jsonlib(JsonObject)提取团购ID
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("id_list");

                    int size = jsonArray.length();
                    if (size > 40) {
                        size = 40;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < size; i++) {
                        String id = jsonArray.getString(i);
                        stringBuilder.append(id).append(",");
                    }
                    //将最后一个多余的“，”号去掉，结果为"1-33946,1-4531,1-4571..."格式
                    String idlist = stringBuilder.substring(0, stringBuilder.length() - 1);

                    //2、获取团购详情
                    Map<String, String> params2 = new HashMap<String, String>();
                    params2.put("deal_ids", idlist);
                    String url2 = HttpUtil.getURL("http://api.dianping.com/v1/deal/get_batch_deals_by_id", params2);
                    StringRequest request2 = new StringRequest(url2, listener, null);

                    //将请求对象放入请求队列中去
                    queue.add(request2);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, null);
        queue.add(request1);
    }
}
