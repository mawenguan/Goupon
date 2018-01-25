package com.mwg.goupon.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mwg.goupon.R;
import com.mwg.goupon.adapter.DealAdapter;
import com.mwg.goupon.bean.TuanBean;
import com.mwg.goupon.util.HttpUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends Activity {

    //头部
    @BindView(R.id.ll_main_city)
    LinearLayout cityContainer;
    @BindView(R.id.tv_main_city)
    TextView textViewCity;
    @BindView(R.id.iv_main_add)
    ImageView imageViewAdd;
    @BindView(R.id.inflate_main_add)
    View addMenu;

    //中部
    @BindView(R.id.pull_to_refresh_main)
    PullToRefreshListView pullToRefreshListView;
    ListView listView;
    List<TuanBean.Deal> datas;
    DealAdapter adapter;

    //底部
    @BindView(R.id.rg_bottom_menu)
    RadioGroup bottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initListView();
    }

    @OnClick(R.id.ll_main_city)
    public void jumpToCity(View view) {
        Intent intent = new Intent(this, CityActivity.class);
        startActivityForResult(intent, 100);
    }

    @OnClick(R.id.iv_main_add)
    public void toggleMenu(View view) {
        if (imageViewAdd.getVisibility() == View.VISIBLE) {
            imageViewAdd.setVisibility(View.INVISIBLE);
        } else {
            imageViewAdd.setVisibility(View.VISIBLE);
        }
    }

    private void initListView() {
        //1、初始化pullToRefreshListView
        listView = pullToRefreshListView.getRefreshableView();
        datas = new ArrayList<TuanBean.Deal>();
        adapter = new DealAdapter(this, datas);
        listView.setAdapter(adapter);

        //2、ListView添加若干个设计好的头部布局控件
        LayoutInflater inflater = LayoutInflater.from(this);
        View inflateFirstView = inflater.inflate(
                R.layout.content_header_main_layout, listView, false);
        View inflateSecondView = inflater.inflate(
                R.layout.content_header_square, listView, false);
        View inflateThirdView = inflater.inflate(
                R.layout.content_header_adds, listView, false);
        View inflateFourthView = inflater.inflate(
                R.layout.content_header_categories, listView, false);
        View inflateFifthView = inflater.inflate(
                R.layout.content_header_recommend, listView, false);

        listView.addHeaderView(inflateFirstView);
        listView.addHeaderView(inflateSecondView);
        listView.addHeaderView(inflateThirdView);
        listView.addHeaderView(inflateFourthView);
        listView.addHeaderView(inflateFifthView);

        initInflateFirstView(inflateFirstView);

        //添加下拉松手后的刷新
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        datas.add(0, "新增内容");
//                        adapter.notifyDataSetChanged();
//                        pullToRefreshListView.onRefreshComplete();
//                  }
//                }, 1500);
                refresh();
            }
        });
        //为ListView添加滑动监听，以隐藏搜索框
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    cityContainer.setVisibility(View.VISIBLE);
                    imageViewAdd.setVisibility(View.VISIBLE);
                } else {
                    cityContainer.setVisibility(View.GONE);
                    imageViewAdd.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initInflateFirstView(final View inflateFirstView) {
        final ViewPager viewPager = inflateFirstView.findViewById(
                R.id.vp_content_header_main);
        PagerAdapter adapter = new PagerAdapter() {

            int[] resIDs = new int[]{
                    R.layout.content_header_views_1,
                    R.layout.content_header_views_2,
                    R.layout.content_header_views_3,
            };

            @Override
            public int getCount() {
                return 30000;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                int layoutID = resIDs[position % 3];
                View view = LayoutInflater.from(MainActivity.this).
                        inflate(layoutID, viewPager, false);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(15000);

        final ImageView iv1 = inflateFirstView.findViewById(
                R.id.iv_content_header_main_indicator_1);
        final ImageView iv2 = inflateFirstView.findViewById(
                R.id.iv_content_header_main_indicator_2);
        final ImageView iv3 = inflateFirstView.findViewById(
                R.id.iv_content_header_main_indicator_3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                iv1.setImageResource(R.drawable.banner_dot);
                iv2.setImageResource(R.drawable.banner_dot);
                iv3.setImageResource(R.drawable.banner_dot);

                switch (position % 3) {
                    case 0:
                        iv1.setImageResource(R.drawable.banner_dot_pressed);
                        break;
                    case 1:
                        iv2.setImageResource(R.drawable.banner_dot_pressed);
                        break;
                    case 2:
                        iv3.setImageResource(R.drawable.banner_dot_pressed);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        String cityName = getIntent().getStringExtra("cityName");
//        if (!TextUtils.isEmpty(cityName)){
//            textViewCity.setText(cityName);
//        }else {
//            textViewCity.setText("北京");
//        }
        refresh();

    }

    private void refresh() {
//        datas.add("111");
//        datas.add("222");
//        datas.add("333");
//        adapter.notifyDataSetChanged();

        //HttpUtil.testHttpURLConnection();
        //HttpUtil.testVolley();
        //HttpUtil.testRetrofit();


//        HttpUtil.getDailyDealsByVolley(textViewCity.getText().toString(), new Response.Listener<TuanBean>() {
//            @Override
//            public void onResponse(TuanBean s) {
//                //Log.d("TAG:", "Volley方式访问的团购详情" + s);
//                if (s!=null) {
//                    List<TuanBean.Deal> deals = s.getDeals();
//                    //将deals放到ListView中呈现
//                    adapter.addAll(deals, true);
//                }else {
//                    //今日无新增团购内容
//                    Toast.makeText(MainActivity.this,"今日无新增团购内容",Toast.LENGTH_LONG).show();
//                }
//                pullToRefreshListView.onRefreshComplete();
//            }
//        });


        HttpUtil.getDailyDealsByRetrofit(textViewCity.getText().toString(), new Callback<TuanBean>() {
            @Override
            public void onResponse(Call<TuanBean> call, retrofit2.Response<TuanBean> response) {
                if (response != null) {
                    TuanBean tuanBean = response.body();
                    List<TuanBean.Deal> deals = tuanBean.getDeals();
                    adapter.addAll(deals, true);
                } else {
                    Toast.makeText(MainActivity.this, "今日无新增团购信息！", Toast.LENGTH_LONG).show();
                }
                pullToRefreshListView.onRefreshComplete();
            }

            @Override
            public void onFailure(Call<TuanBean> call, Throwable throwable) {
                Log.d("Tag:", "onFailure" + throwable.getMessage());
                pullToRefreshListView.onRefreshComplete();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            String city = data.getStringExtra("cityname");
            textViewCity.setText(city);
        }
    }
}
