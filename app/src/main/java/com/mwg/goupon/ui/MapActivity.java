package com.mwg.goupon.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.mwg.goupon.R;
import com.mwg.goupon.constant.BusinessBean;

import java.nio.charset.CoderResult;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends Activity {

    BusinessBean.Business business;

    //声明相框
    @BindView(R.id.bmapView)
    public MapView mMapView;

    //声明相片
    BaiduMap baiduMap;

    String from;//main,detail

    public LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        from = getIntent().getStringExtra("from");
        business = (BusinessBean.Business) getIntent().getSerializableExtra("business");
        ButterKnife.bind(this);
        baiduMap = mMapView.getMap();

        //更改地图默认的比例尺（从5km--->100m）
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17);
        baiduMap.animateMapStatus(msu);

//        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                return true;
//            }
//        });

        if ("main".equals(from)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //判段是否已获得权限，若为否，则发起权限请求来获得
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 100);
                } else {
                    showMyLocation();
                }
            } else {
                showMyLocation();
            }
            showMyLocation();//进行定位
        } else {
            showAddress();
        }
        showAddress();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100){
            showMyLocation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stop();
            mLocationClient.unRegisterLocationListener(myListener);
            mLocationClient = null;
        }
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    /**
     * 在百度地图上显示某地址
     */
    private void showAddress() {
        //根据查询到的地址，获得所对应的经纬度
        //（根据经纬度，反查具体地址——称为反向地理编码查询）
        GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null && geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(MapActivity.this,
                            "服务区繁忙，请稍后重试！", Toast.LENGTH_LONG).show();
                } else {
                    //1、获得地址所对应的经纬度
                    LatLng location = geoCodeResult.getLocation();

                    //2、在location所对应的经纬度插上一个标志物
                    MarkerOptions option = new MarkerOptions();
                    option.position(location);
                    option.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_locate));
                    baiduMap.addOverlay(option);

                    //3、移动屏幕的中心点到location所对应的位置
                    MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(location);
                    baiduMap.animateMapStatus(msu);

                    //4、添加一个信息窗
                    TextView tv = new TextView(MapActivity.this);
                    tv.setText(business.getAddress());
                    tv.setPadding(8, 8, 8, 8);
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.BLUE);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

                    InfoWindow infoWindow = new InfoWindow(tv, location, -50);
                    baiduMap.showInfoWindow(infoWindow);
                }

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
        //正真发起地理编码查询
        GeoCodeOption option = new GeoCodeOption();
        option.address(business.getAddress());
        option.city(business.getCity());
        geoCoder.geocode(option);
    }


    private void showMyLocation() {
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();

        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        option.setIsNeedAddress(true);

        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            int type = bdLocation.getLocType();
            double lat = -1;
            double lng = -1;

            if (type == 61 || type == 65 || type == 66 || type == 161) {
                //定位成功了
                lat = bdLocation.getLatitude();
                lng = bdLocation.getLongitude();
            } else {
                //定位失败了
                //手动指定一个位置
                lat = 39.895316;
                lng = 116.207757;
            }
            //1、添加标志物

            //2、移动屏幕中心点

            //3、信息窗（“我在这儿”）

            //4、停止定位

        }
    }

}
