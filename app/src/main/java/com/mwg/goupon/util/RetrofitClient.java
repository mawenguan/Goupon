package com.mwg.goupon.util;

import android.util.Log;

import com.mwg.goupon.bean.TuanBean;
import com.mwg.goupon.constant.Constant;
import com.mwg.goupon.ui.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by mwg on 2018/1/15.
 */

public class RetrofitClient {

    private static RetrofitClient INSTANCE;

    public static RetrofitClient getInstance() {
        if (INSTANCE == null) {
            synchronized (RetrofitClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RetrofitClient();
                }
            }
        }
        return INSTANCE;
    }


    private Retrofit retrofit;
    private NetService netService;

    private RetrofitClient() {
        retrofit = new Retrofit.Builder().baseUrl(Constant.BASEURL).
                addConverterFactory(ScalarsConverterFactory.create()).
                addConverterFactory(GsonConverterFactory.create()).build();
        netService = retrofit.create(NetService.class);
    }

    public void test() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("city", "北京");
        params.put("category", "美食");
        String sign = HttpUtil.getSign(HttpUtil.APPKEY, HttpUtil.APPSECRET, params);
        Call<String> call = netService.test(HttpUtil.APPKEY, sign, params);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String string = response.body();
                Log.i("TAG:", "利用封装后的Retrofit获得响应");
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {

            }
        });
    }

    public void getDailyDealsFromRetrofit(String city, final Callback<TuanBean> callback){

        final Map<String, String> params = new HashMap<String, String>();
        params.put("city", city);
        params.put("date", new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()));
        String Sign = HttpUtil.getSign(HttpUtil.APPKEY, HttpUtil.APPSECRET, params);
        Call<String> ids = netService.getDailyIds(HttpUtil.APPKEY, Sign, params);

        ids.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                //利用Jsonlib(JsonObject)提取团购ID
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
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
                    if (stringBuilder.length() > 0) {
                        //将最后一个多余的“，”号去掉，结果为"1-33946,1-4531,1-4571..."格式
                        String idlist = stringBuilder.substring(0, stringBuilder.length() - 1);

                        Map<String, String> params2 = new HashMap<String, String>();
                        params2.put("deal_ids", idlist);
                        String sign2 = HttpUtil.getSign(HttpUtil.APPKEY, HttpUtil.APPSECRET, params2);
                        Call<TuanBean> call2 = netService.getDealsFromRetrofit(HttpUtil.APPKEY, sign2, params2);
                        call2.enqueue(callback);
                    } else {
                        //该城市今日无新增团购信息
                        callback.onResponse(null, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {

            }
        });

    }

    public void getDailyDeals(String city, final Callback<String> callback2) {

        final Map<String, String> params = new HashMap<String, String>();
        params.put("city", city);
        params.put("date", new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()));
        String Sign = HttpUtil.getSign(HttpUtil.APPKEY, HttpUtil.APPSECRET, params);
        Call<String> ids = netService.getDailyIds(HttpUtil.APPKEY, Sign, params);

        ids.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                //利用Jsonlib(JsonObject)提取团购ID
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
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
                    if (stringBuilder.length() > 0) {
                        //将最后一个多余的“，”号去掉，结果为"1-33946,1-4531,1-4571..."格式
                        String idlist = stringBuilder.substring(0, stringBuilder.length() - 1);

                        Map<String, String> params2 = new HashMap<String, String>();
                        params2.put("deal_ids", idlist);
                        String sign2 = HttpUtil.getSign(HttpUtil.APPKEY, HttpUtil.APPSECRET, params2);
                        Call<String> call2 = netService.getDeals(HttpUtil.APPKEY, sign2, params2);
                        call2.enqueue(callback2);
                    } else {
                        //该城市今日无新增团购信息
                        callback2.onResponse(null, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {

            }
        });
    }
}
