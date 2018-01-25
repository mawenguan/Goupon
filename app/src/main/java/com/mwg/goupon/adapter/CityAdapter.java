package com.mwg.goupon.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.mwg.goupon.R;
import com.mwg.goupon.bean.CityNameBean;

import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mwg on 2018/1/18.
 */

public class CityAdapter extends
        RecyclerView.Adapter<CityAdapter.MyViewHolder> implements SectionIndexer {

    //同ListView，在数据适配时要先声明三个属性
    Context context;
    List<CityNameBean> datas;
    LayoutInflater inflater;
    //为RecyclerView添加一个条目监听
    OnItemClickListener listener;
    //为RecyclerView添加一个头部视图
    View headerView;

    public static final int HEADER = 0;
    public static final int ITEM = 1;

    public CityAdapter(Context context, List<CityNameBean> datas) {
        this.context = context;
        this.datas = datas;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void addHeaderView(View v) {
        if (this.headerView == null) {
            this.headerView = v;
            notifyItemChanged(0);
        } else {
            throw new RuntimeException("不允许添加多个头部！");
        }
    }

    public View getHeaderView(){
        return headerView;
    }

    @Override
    public int getItemViewType(int position) {
//        if (headerView!=null){
//            if (position==0){
//                return HEADER;
//            }else {
//                return ITEM;
//            }
//        }else {
//            return ITEM;
//        }
        if (headerView != null && position == 0) {
            return HEADER;
        }
        return ITEM;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //
        if (viewType == HEADER) {
            MyViewHolder myViewHolder = new MyViewHolder(headerView);
            return myViewHolder;
        }
        //创建ViewHolder对象
        View view = inflater.inflate(R.layout.inflate_item_cityname_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //避免当条目为headerView时，以下代码发生空指针异常
        if (headerView != null && position == 0) {
            return;
        }

        final int dataIndex = getDataIndex(position);

        //将第position位置的数据放到ViewHolder
        CityNameBean cityNameBean = datas.get(dataIndex);
        holder.tvName.setText(cityNameBean.getCityName());
        holder.tvLetter.setText(cityNameBean.getLetter() + "");

        //position这个位置的数据是不是该数据所在分组的起始位置
        if (dataIndex == getPositionForSection(getSectionForPosition(dataIndex))) {
            holder.tvLetter.setVisibility(View.VISIBLE);
        } else {
            holder.tvLetter.setVisibility(View.GONE);
        }

        View itemView = holder.itemView;
        if (itemView != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(v, dataIndex);
                }
            });
        }
    }

    private int getDataIndex(int position) {
        return headerView == null ? position : (position - 1);
    }

    @Override
    public int getItemCount() {
        int count = datas.size();
        Log.d("TAG:", "总的城市数量：" + count);
        return count;
    }

    public void addAll(List<CityNameBean> list, boolean isClear) {
        if (isClear) {
            datas.clear();
        }
        datas.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 获得某一个分组的起始位置是什么
     *
     * @param sectionIndex
     * @return
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).getLetter() == sectionIndex) {
                return i;
            }
        }
        //当用户输入的数据在整个分组都找不到时返回的值
        return datas.size() + 1;
    }

    /**
     * 获得position所在位置的分组的sectionIndex
     *
     * @param position
     * @return
     */
    @Override
    public int getSectionForPosition(int position) {
        return datas.get(position).getLetter();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        //利用butter knife完成对View Holder中控件的赋值
        //显示城市拼音首字母
        @Nullable
        @BindView(R.id.tv_city_letter)
        TextView tvLetter;
        //显示城市中文名称
        @Nullable
        @BindView(R.id.tv_city_name)
        TextView tvName;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View itemView, int position);
    }
}
