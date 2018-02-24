package com.mwg.goupon.util;

import com.mwg.goupon.bean.CityBean;
import com.mwg.goupon.bean.DistrictBean;
import com.mwg.goupon.bean.TuanBean;
import com.mwg.goupon.constant.BusinessBean;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
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

    @GET("deal/get_daily_new_id_list")
    public Call<String> getDailyIds(@Query("appkey") String appkey,
            @Query("sign") String sign,@QueryMap Map<String,String> params);

    @GET("deal/get_batch_deals_by_id")
    public Call<String> getDeals(@Query("appkey") String appkey,
            @Query("sign") String sign,@QueryMap Map<String,String> params);


    @GET("deal/get_daily_new_id_list")
    public Call<String> getDailyIds2(@QueryMap Map<String,String> params);

    @GET("deal/get_batch_deals_by_id")
    public Call<TuanBean> getDeals2(@QueryMap Map<String,String> params);


    @GET("deal/get_batch_deals_by_id")
    public Call<TuanBean> getDealsFromRetrofit(@Query("appkey") String appkey,
            @Query("sign") String sign, @QueryMap Map<String,String> params);

    @GET("metadata/get_cities_with_businesses")
    public Call<CityBean> getCities();

    @GET("business/find_businesses")
    public Call<BusinessBean> getFoods(@QueryMap Map<String,String> params);

    @GET("metadata/get_regions_with_businesses")
    public Call<DistrictBean> getDistricts(@QueryMap Map<String,String> params);
}
