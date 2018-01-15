package com.mwg.goupon.util;

import android.provider.CallLog;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by mwg on 2018/1/15.
 */

public interface NetService {

    @GET("business/find_businesses")
    public Call<String> test(@Query("appkey") String appkey,
            @Query("sign")String sign, @QueryMap Map<String,String> params);

}
