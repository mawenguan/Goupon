package com.mwg.goupon.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.mwg.goupon.R;
import com.mwg.goupon.adapter.BusinessAdapter;
import com.mwg.goupon.bean.DistrictBean;
import com.mwg.goupon.bean.TuanBean;
import com.mwg.goupon.constant.BusinessBean;
import com.mwg.goupon.util.HttpUtil;
import com.mwg.goupon.util.SharedPreferencesUtil;
import com.mwg.goupon.view.MyBannerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;

public class BusinessActivity extends Activity {

    String city;

    @BindView(R.id.lv_businessactivity_item)
    ListView listView;
    List<BusinessBean.Business> datas;
    BusinessAdapter adapter;

    SharedPreferencesUtil sPUtil;

    @BindView(R.id.iv_business_loading)
    ImageView ivLoading;

    @BindView(R.id.district_layout)
    View districtLayout;
    @BindView(R.id.tv_businessheader_near)
    TextView tvNear;

    @BindView(R.id.lv_business_select_left)
    ListView lvLeft;
    @BindView(R.id.lv_business_select_right)
    ListView lvRight;

    List<String> leftDatas;
    List<String> rightDatas;

    ArrayAdapter<String> leftAdapter;
    ArrayAdapter<String> rightAdapter;

    List<DistrictBean.City.District> districtList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        city = getIntent().getStringExtra("city");
        Log.d("TAG:", "onCreate:城市--->" + city);

        ButterKnife.bind(this);
        sPUtil = new SharedPreferencesUtil(this);
        initListView();
    }

    private void initListView() {

        datas = new ArrayList<BusinessBean.Business>();
        adapter = new BusinessAdapter(this, datas);

        if (!sPUtil.isCloseBanner()) {
            final MyBannerView myBannerView = new MyBannerView(this, null);
            myBannerView.setOnCloseBannerListener(new MyBannerView.OnCloseBannerListener() {
                @Override
                public void onClose() {
                    sPUtil.setCloseBanner(true);
                    listView.removeHeaderView(myBannerView);
                }
            });
            listView.addHeaderView(myBannerView);
        }
        listView.setAdapter(adapter);

        //由于是帧动画，需要先让帧动画播放起来
        AnimationDrawable drawable = (AnimationDrawable) ivLoading.getDrawable();
        drawable.start();

        listView.setEmptyView(ivLoading);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BusinessBean.Business business;

                //判断ListView是否有头部，以做数据适配
                if (sPUtil.isCloseBanner()) {
                    business = adapter.getItem(position);
                } else {
                    business = adapter.getItem(position - 1);
                }

                Intent intent = new Intent(BusinessActivity.this, DetailActivity.class);
                intent.putExtra("business", business);
                startActivity(intent);
            }
        });

        leftDatas = new ArrayList<String>();
        leftAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, leftDatas);
        lvLeft.setAdapter(leftAdapter);

        rightDatas = new ArrayList<String>();
        rightAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rightDatas);
        lvRight.setAdapter(rightAdapter);

        lvLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //拿到用户所点选的那个区
                DistrictBean.City.District district = districtList.get(position);
                List<String> neighborhoods = new ArrayList<>(district.getNeighborhoods());
                neighborhoods.add(0, "全部" + district.getDistrict_name());

                rightDatas.clear();
                rightDatas.addAll(neighborhoods);
                rightAdapter.notifyDataSetChanged();
            }
        });

        lvRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String region = rightAdapter.getItem(position);
                if (position == 0) {//region“全部XXX区”
                    region = region.substring(2, region.length());
                }
                tvNear.setText(region);
                districtLayout.setVisibility(View.INVISIBLE);

                adapter.removeAll();//清空原有数据

                HttpUtil.getFoodsByRetrofit(city, region, new Callback<BusinessBean>() {
                    @Override
                    public void onResponse(Call<BusinessBean> call, retrofit2.Response<BusinessBean> response) {

                        BusinessBean businessBean = response.body();
                        List<BusinessBean.Business> businesses = businessBean.getBusinesses();
                        adapter.addAll(businesses, true);
                    }

                    @Override
                    public void onFailure(Call<BusinessBean> call, Throwable throwable) {

                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {

//        HttpUtil.getFoodsByVolley(city,null , new Response.Listener<String>() {
//            @Override
//            public void onResponse(String s) {
//
//            }
//        });
        HttpUtil.getFoodsByRetrofit(city, null, new Callback<BusinessBean>() {
            @Override
            public void onResponse(Call<BusinessBean> call, retrofit2.Response<BusinessBean> response) {
                BusinessBean business = response.body();
                List<BusinessBean.Business> businessLists = business.getBusinesses();
                adapter.addAll(businessLists, true);
            }

            @Override
            public void onFailure(Call<BusinessBean> call, Throwable throwable) {

            }
        });

        HttpUtil.getDistrictsByRetrofit(city, new Callback<DistrictBean>() {
            @Override
            public void onResponse(Call<DistrictBean> call, retrofit2.Response<DistrictBean> response) {
                DistrictBean districtBean = response.body();
                districtList = districtBean.getCities().get(0).getDistricts();

                List<String> districtNames = new ArrayList<String>();
                for (int i = 0; i < districtList.size(); i++) {
                    districtNames.add(districtList.get(i).getDistrict_name());
                }
                //左侧城市的区的数据适配
                leftDatas.clear();
                leftDatas.addAll(districtNames);
                leftAdapter.notifyDataSetChanged();

                //右侧热门商区的数据适配
                List<String> neighborhoods = new ArrayList<String>(districtList.get(0).getNeighborhoods());
                String districtName = districtList.get(0).getDistrict_name();
                neighborhoods.add(0, "全部" + districtName);

                rightDatas.clear();
                rightDatas.addAll(neighborhoods);
                leftAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<DistrictBean> call, Throwable throwable) {

            }
        });
    }

    @OnClick(R.id.tv_businessheader_near)
    public void showDistricts(View view) {
        if (districtLayout.getVisibility() != View.VISIBLE) {
            districtLayout.setVisibility(View.VISIBLE);
        } else {
            districtLayout.setVisibility(View.INVISIBLE);
        }
    }
}
