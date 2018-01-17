package com.mwg.goupon.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mwg.goupon.R;
import com.mwg.goupon.app.MyApp;
import com.mwg.goupon.bean.TuanBean;
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
    ImageLoader imageLoader;

    //3、构造器私有化
    private VolleyClient() {
        queue = Volley.newRequestQueue(MyApp.CONTEXT);
        imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
            //least recently use
            LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>
                    ((int) (Runtime.getRuntime().maxMemory() / 8)) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getHeight() * value.getRowBytes();
                }
            };

            @Override
            public Bitmap getBitmap(String s) {
                return cache.get(s);
            }

            @Override
            public void putBitmap(String s, Bitmap bitmap) {
                cache.put(s, bitmap);
            }
        });
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
                    if (stringBuilder.length() > 0) {
                        //将最后一个多余的“，”号去掉，结果为"1-33946,1-4531,1-4571..."格式
                        String idlist = stringBuilder.substring(0, stringBuilder.length() - 1);

                        //2、获取团购详情
                        Map<String, String> params2 = new HashMap<String, String>();
                        params2.put("deal_ids", idlist);
                        String url2 = HttpUtil.getURL("http://api.dianping.com/v1/deal/get_batch_deals_by_id", params2);
                        StringRequest request2 = new StringRequest(url2, listener, null);

                        //将请求对象放入请求队列中去
                        queue.add(request2);
                    } else {
                        //该城市今日无新增团购信息
                        listener.onResponse(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, null);
        queue.add(request1);
    }

    /**
     * 显示网络中的一副图片
     *
     * @param url 图片在网络中的地址
     * @param iv  显示的图片
     */
    public void loadImage(String url, ImageView iv) {

        ImageLoader.ImageListener listener = ImageLoader.getImageListener(iv,
                R.drawable.bucket_no_picture, R.drawable.bucket_no_picture);
        imageLoader.get(url, listener);
    }

    public void getDailyDeals2(String city, final Response.Listener<TuanBean> listener){
        //1、获取新增团购的ID列表
        final Map<String, String> params1 = new HashMap<String, String>();
        params1.put("city", city);
        params1.put("date", new SimpleDateFormat("yyyy-MM-dd").
                format(System.currentTimeMillis()));
        final String url1 = HttpUtil.getURL("http://api.dianping.com/v1/deal/get_daily_new_id_list", params1);
        StringRequest request1 = new StringRequest(url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
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
                    if (stringBuilder.length() > 0) {
                        //将最后一个多余的“，”号去掉，结果为"1-33946,1-4531,1-4571..."格式
                        String idlist = stringBuilder.substring(0, stringBuilder.length() - 1);

                        //2、获取团购详情
                        Map<String, String> params2 = new HashMap<String, String>();
                        params2.put("deal_ids", idlist);
                        String url2 = HttpUtil.getURL("http://api.dianping.com/v1/deal/get_batch_deals_by_id", params2);
                        TuanBeanRequest request2 = new TuanBeanRequest(url2, listener);

                        //将请求对象放入请求队列中去
                        queue.add(request2);
                    } else {
                        //该城市今日无新增团购信息
                        listener.onResponse(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, null);
        queue.add(request1);
    }

    /**
     * 自定义请求对象，直接返回一个封装好的实体类对象（又服务器返回的Json字符串解析出来的）
     */
    public class TuanBeanRequest extends Request<TuanBean>{
        //自定义一个监听的属性，并在构造器中对其进行赋值
        Response.Listener<TuanBean> listener;

        public TuanBeanRequest(String url,Response.Listener<TuanBean> listener) {
            super(Method.GET, url, null);
            this.listener=listener;
        }

        @Override
        protected Response<TuanBean> parseNetworkResponse(NetworkResponse networkResponse) {
            //拿到Volley的返回值，将其包装为字符串
            String response = new String(networkResponse.data);
            Gson gson = new Gson();
            TuanBean tuanBean = gson.fromJson(response,TuanBean.class);

            //自己组装一个Volley的Response对象作为方法的返回值
            Response<TuanBean> result = Response.success(tuanBean,
                    HttpHeaderParser.parseCacheHeaders(networkResponse));
            return result;
        }

        @Override
        protected void deliverResponse(TuanBean tuanBean) {
            //调用listener，将tuanBean传过去
            listener.onResponse(tuanBean);
        }
    }
}
