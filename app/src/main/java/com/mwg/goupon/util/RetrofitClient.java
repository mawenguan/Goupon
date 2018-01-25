package com.mwg.goupon.util;

import android.util.Log;

import com.mwg.goupon.bean.CityBean;
import com.mwg.goupon.bean.TuanBean;
import com.mwg.goupon.constant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
    private OkHttpClient okHttpClient;
    private NetService netService;

    private RetrofitClient() {
        okHttpClient = new OkHttpClient.Builder().addInterceptor(new MyOkHttpInterceptor()).build();
        retrofit = new Retrofit.Builder().client(okHttpClient).baseUrl(Constant.BASEURL).
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

    public void getDailyDealsFromRetrofit(String city, final Callback<TuanBean> callback) {

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

    public void getDailyDealsFromRetrofit2(String city, final Callback<TuanBean> callback2) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("city", city);
        params.put("date", new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()));
        final Call<String> idcall = netService.getDailyIds2(params);
        idcall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
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
                    if (stringBuilder.length()>0){
                        String idlist = stringBuilder.substring(0,stringBuilder.length()-1);

                        Map<String, String> params2 = new HashMap<String, String>();
                        params2.put("deal_ids",idlist);
                        Call<TuanBean> deals2 = netService.getDeals2(params2);
                        deals2.enqueue(callback2);
                    }else {
                        callback2.onResponse(null,null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {

            }
        });
    }

    public void getCities(Callback<CityBean> callback) {
        Call<CityBean> call = netService.getCities();
        call.enqueue(callback);
    }

    /**
     * OKHTTP拦截器
     */
    public class MyOkHttpInterceptor implements Interceptor {

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            //获得请求对象
            Request request = chain.request();
            //请求链接/请求路径，比如：BASEURL/metadata/get_cities_with_businesses
            HttpUrl url = request.url();

            //准备一个集合，用于存放取出的除原有路径中的参数
            HashMap<String, String> params = new HashMap<String, String>();
            //原有请求路径中，请求参数的名称：如{city，date}
            Set<String> set = url.queryParameterNames();
            for (String key : set) {
                params.put(key, url.queryParameter(key));
            }

            String sign = HttpUtil.getSign(HttpUtil.APPKEY, HttpUtil.APPSECRET, params);

            //得到一个字符串格式的url
            String urlString = url.toString();
//            Log.d("TAG:", "原始请求路径" + urlString);

            StringBuilder sb = new StringBuilder(urlString);
            if (set.size()==0){
                //意味着原有请求路径中没有参数
                sb.append("?");
            }else {
                sb.append("&");
            }
            sb.append("appkey=").append(HttpUtil.APPKEY);
            sb.append("&").append("sign=").append(sign);
//            Log.d("TAG:", "新的请求路径" + sb.toString());

            Request newRequest = new Request.Builder().url(sb.toString()).build();
            return chain.proceed(newRequest);
        }
    }

}
