package com.mwg.goupon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by mwg on 2018/1/16.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<T> datas;

    public MyBaseAdapter(Context context, List<T> datas) {
        this.context = context;
        this.datas = datas;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public T getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 追加一个新数据
     * @param t
     */
    public void add(T t){
        datas.add(t);
        notifyDataSetChanged();
    }

    /**
     * 追加或者替换数据
     * @param list
     * @param isClear
     */
    public void addAll(List<T> list,boolean isClear) {
        if (isClear) {
            datas.clear();
        }
        datas.addAll(list);
        notifyDataSetChanged();
    }

    //清除所有数据
    public void removeAll(){
        datas.clear();
        notifyDataSetChanged();
    }
}
