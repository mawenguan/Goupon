package com.mwg.goupon.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mwg.goupon.R;
import com.mwg.goupon.app.MyApp;
import com.mwg.goupon.bean.CityNameBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;

public class SearchActivity extends Activity {

    @BindView(R.id.lv_search_listview)
    ListView listView;
    List<String> cities;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);
        initListView();
    }

    private void initListView() {
        cities = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cities);
        listView.setAdapter(adapter);
    }

    @OnTextChanged(R.id.et_search)
    public void search(Editable editable){
        if(editable.length()==0){
            cities.clear();
            adapter.notifyDataSetChanged();
        }else{
            searchCities(editable.toString().toUpperCase());
        }
    }

    /**
     * 根据输入的内容
     * 筛选符合的城市名称
     * @param s
     */
    private void searchCities(String s) {

        List<String> temps = new ArrayList<String>();
        //中文 char 16bit 0-65535
        if(s.matches("[\u4e00-\u9fff]+")){
            for(CityNameBean bean: MyApp.cityNameBeanList){
                if(bean.getCityName().contains(s)){
                    temps.add(bean.getCityName());
                }
            }
        }else{//拼音
            for(CityNameBean c:MyApp.cityNameBeanList){
                if(c.getPyName().contains(s)){
                    temps.add(c.getCityName());
                }
            }
        }

        if(temps.size() > 0){
            cities.clear();
            cities.addAll(temps);
            adapter.notifyDataSetChanged();
        }
    }

    @OnItemClick(R.id.lv_search_listview)
    public void selectCity(AdapterView<?> adapterView, View view, int i, long l){
        Intent data = new Intent();
        String cityName = adapter.getItem(i);
        data.putExtra("cityname",cityName);
        setResult(RESULT_OK,data);
        finish();
    }
}
