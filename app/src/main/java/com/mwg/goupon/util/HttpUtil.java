package com.mwg.goupon.util;

import android.widget.ImageView;

import com.android.volley.Response;
import com.mwg.goupon.R;
import com.mwg.goupon.app.MyApp;
import com.mwg.goupon.bean.CityBean;
import com.mwg.goupon.bean.TuanBean;
import com.mwg.goupon.util.RetrofitClient;
import com.mwg.goupon.util.VolleyClient;
import com.squareup.picasso.Picasso;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Callback;

/**
 * Created by mwg on 2018/1/14.
 */

public class HttpUtil {
    public static final String APPKEY = "49814079";
    public static final String APPSECRET = "90e3438a41d646848033b6b9d461ed54";

    //获得满足大众点评服务器要求的请求路径
    public static String getURL(String url, Map<String, String> params) {

        String result = "";

        String sign = getSign(APPKEY, APPSECRET, params);

        String query = getQuery(APPKEY, sign, params);

        result = url + "?" + query;

        return result;
    }

    /**
     * 获取请求地址中的签名
     *
     * @param appkey
     * @param appsecret
     * @param params
     * @return
     */
    public static String getSign(String appkey, String appsecret, Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder();
        // 对参数名进行字典排序
        String[] keyArray = params.keySet().toArray(new String[0]);
        Arrays.sort(keyArray);
        // 拼接有序的参数名-值串
        stringBuilder.append(appkey);
        for (String key : keyArray) {
            stringBuilder.append(key).append(params.get(key));
        }
        String codes = stringBuilder.append(appsecret).toString();
        //在纯JAVA环境下，利用codec对字符串进行SHA1转码采用如下方式
        //String sign = org.apache.commons.codec.digest.DigestUtils.shaHex(codes).toUpperCase();
        //在纯ANDROID环境下，利用codec对字符串进行SHA1转码采用如下方式：
        String sign = new String(Hex.encodeHex(DigestUtils.sha(codes))).toUpperCase();
        return sign;
    }

    /**
     * 获取请求地址中的参数部分
     *
     * @param appkey
     * @param sign
     * @param params
     * @return
     */
    public static String getQuery(String appkey, String sign, Map<String, String> params) {
        // 添加签名
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("appkey=").append(appkey).append("&sign=").append(sign);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                stringBuilder.append('&').append(entry.getKey()).append('=').
                        append(URLEncoder.encode(entry.getValue(), "utf8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                //抛出异常
                throw new RuntimeException("使用了不正确de字符集名称");
            }
        }
        String queryString = stringBuilder.toString();
        return queryString;
    }

    public static void testHttpURLConnection() {
        //获取符合大众点评要去的请求地址
        Map<String, String> params = new HashMap<String, String>();
        params.put("city", "北京");
        params.put("category", "美食");
        final String url = getURL("http://api.dianping.com/v1/business/find_businesses", params);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL u = new URL(url);
                    HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);//可写可不写，默认是true
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb = new StringBuilder();
                    String line = "";
                    while ((line = bf.readLine()) != null) {
                        sb.append(line);
                    }
                    bf.close();
                    String response = sb.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void testVolley() {
//        VolleyClient client = new VolleyClient();
//        client.test();

        VolleyClient.getInstance().test();
    }

    public static void testRetrofit() {
//        //1、创建Retrofit对象
//        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.dianping.com/v1/").
//                addConverterFactory(ScalarsConverterFactory.create()).build();
//
//        //2、创建接口的实现类对象
//        NetService service = retrofit.create(NetService.class);
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("city", "北京");
//        params.put("category", "美食");
//        String sign = getSign(APPKEY, APPSECRET, params);
//
//        //3、获得请求对象
//        Call<String> call = service.test(HttpUtil.APPKEY, sign, params);
//        //4、将请求对象扔到请求队列中
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
//                String string = response.body();
//                Log.i("TAG:", "利用Retrofit获得响应：" + string);
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable throwable) {
//
//            }
//        });
        RetrofitClient.getInstance().test();
    }

    public static void getDailyDealsByVolley(String city, Response.Listener<TuanBean> listener){
        VolleyClient.getInstance().getDailyDeals2(city,listener);
    }

    public static void getDailyDealsByRetrofit(String city, Callback<TuanBean> callback){
        RetrofitClient.getInstance().getDailyDealsFromRetrofit2(city,callback);
    }

    public static void loadImage(String url, ImageView iv){
        VolleyClient.getInstance().loadImage(url,iv);
    }

    public static void displayImage(String url, ImageView iv){
        Picasso.with(MyApp.CONTEXT).load(url).placeholder(R.drawable.bucket_no_picture).
                error(R.drawable.bucket_no_picture).into(iv);
    }

    public static void getCitiesByVolley(Response.Listener<String> listener){
        VolleyClient.getInstance().getCitiesByVolley(listener);
    }

    public static void getCitiesByRetrofit(Callback<CityBean> callback){
        RetrofitClient.getInstance().getCities(callback);
    }


}
