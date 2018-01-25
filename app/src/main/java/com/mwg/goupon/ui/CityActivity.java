package com.mwg.goupon.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mwg.goupon.R;
import com.mwg.goupon.adapter.CityAdapter;
import com.mwg.goupon.app.MyApp;
import com.mwg.goupon.bean.CityBean;
import com.mwg.goupon.bean.CityNameBean;
import com.mwg.goupon.util.DBUtil;
import com.mwg.goupon.util.HttpUtil;
import com.mwg.goupon.util.PinYinUtil;
import com.mwg.goupon.view.MyLetterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;

public class CityActivity extends Activity {

    @BindView(R.id.rv_city_cities)
    RecyclerView recyclerView;
    //适配器
    CityAdapter cityAdapter;
    //数据源
    List<CityNameBean> cities;

    DBUtil dbUtil;

    @BindView(R.id.mlv_city_letter)
    MyLetterView myLetterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        dbUtil = new DBUtil(this);
        ButterKnife.bind(this);

        initRecyclerView();
        myLetterView.setOnTouchLetterListener(new MyLetterView.OnTouchLetterListener() {
            @Override
            public void onTouchLetter(MyLetterView view, String letter) {
//              Toast.makeText(CityActivity.this,letter,Toast.LENGTH_SHORT).show();
                LinearLayoutManager manager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();
                if ("热门".equals(letter)) {
                    manager.scrollToPosition(0);
                } else {
                    int position = cityAdapter.getPositionForSection(letter.charAt(0));
//                    int first = manager.findFirstVisibleItemPosition();
//                    int last = manager.findLastVisibleItemPosition();
                    if (cityAdapter.getHeaderView()!=null){
                        position+=1;
                    }
                    //RecyclerView移动到第position个视图位置，且处于最顶端
                    //offset为偏移量，单位为像素值
                    manager.scrollToPositionWithOffset(position, 0);
                }
            }
        });
    }

    private void initRecyclerView() {
        //初始化数据源，适配器
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        cities = new ArrayList<CityNameBean>();
        cityAdapter = new CityAdapter(this, cities);
        recyclerView.setAdapter(cityAdapter);
        View headerView = LayoutInflater.from(this).inflate(
                R.layout.inflate_citylist_header_layout, recyclerView, false);
        cityAdapter.addHeaderView(headerView);
        cityAdapter.setOnItemClickListener(new CityAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View itemView, int position) {
                CityNameBean cityNameBean = cities.get(position);
//              Toast.makeText(CityActivity.this, cityNameBean.getCityName(), Toast.LENGTH_LONG).show();

                String cityName = cityNameBean.getCityName();

//                Intent intent = new Intent(CityActivity.this,MainActivity.class);
//                intent.putExtra("cityName",cityName);
//                startActivity(intent);
                Intent intent = new Intent();
                intent.putExtra("cityname", cityName);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        //从内存缓存中获取城市信息
        if (MyApp.cityNameBeanList != null && MyApp.cityNameBeanList.size() > 0) {
            cityAdapter.addAll(MyApp.cityNameBeanList, true);
            //城市数据是从内从缓存中加载
            Log.d("TAG:", "城市数据是从内从缓存中加载" + MyApp.cityNameBeanList);
            return;
        }

        //从数据库中读取城市数据
        List<CityNameBean> list = dbUtil.query();
        if (list != null && list.size() > 0) {
            cityAdapter.addAll(list, true);

            MyApp.cityNameBeanList = list;
            Log.d("TAG:", "城市数据从数据库中加载");
            return;
        }

        //TODO 调用HttpUtil获取城市信息
//        HttpUtil.getCitiesByVolley(new Response.Listener<String>() {
//            @Override
//            public void onResponse(String s) {
//                Log.d("TAG:",s);
//            }
//        });
        //调用HttpUtil获取沉睡谷hi信息
        HttpUtil.getCitiesByRetrofit(new Callback<CityBean>() {
            @Override
            public void onResponse(Call<CityBean> call, retrofit2.Response<CityBean> response) {
//               Log.d("TAG:", "onResponse:Retrofit--->" + response.body());
                CityBean cityBean = response.body();
                //“全国”“北京”。。。“其他城市”
                List<String> list = cityBean.getCities();
                //根据List<String>创建一个List<CityNameBean>，并将其放到RecyclerView中显示

                final List<CityNameBean> cityNameBeansList = new ArrayList<>();
                for (String name : list) {
                    if (!name.equals("全国") && !name.equals("其它城市")) {
                        CityNameBean cityNameBean = new CityNameBean();
                        cityNameBean.setCityName(name);
                        cityNameBean.setPyName(PinYinUtil.getPinYin(name));
                        cityNameBean.setLetter(PinYinUtil.getPinYin(name).charAt(0));
//                        Log.d("TAG:", "明细：" + cityNameBean);

                        cityNameBeansList.add(cityNameBean);
                    }
                }

                //为城市名称按字典进行排序
                Collections.sort(cityNameBeansList, new Comparator<CityNameBean>() {
                    @Override
                    public int compare(CityNameBean o1, CityNameBean o2) {
                        return o1.getPyName().compareTo(o2.getPyName());
                    }
                });

                cityAdapter.addAll(cityNameBeansList, true);
                //将数据缓存起来
                MyApp.cityNameBeanList = cityNameBeansList;
                //向数据库中写入城市数据
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        long start = System.currentTimeMillis();
                        dbUtil.insertBatch(cityNameBeansList);
                        Log.d("TAG:", "写入数据库完毕,Batch耗时-->" + (System.currentTimeMillis() - start));
                    }
                }.start();
            }

            @Override
            public void onFailure(Call<CityBean> call, Throwable throwable) {

            }
        });
    }

    @OnClick(R.id.tv_city_search)
    public void jumpTo(View view) {
        Intent intent = new Intent(CityActivity.this, SearchActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            //data中取出搜索后点击的城市名称
//            Intent data2 = new Intent();
//            String city = data.getStringExtra("city");
//            data2.putExtra("city",city);
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
