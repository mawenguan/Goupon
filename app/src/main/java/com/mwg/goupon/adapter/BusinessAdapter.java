package com.mwg.goupon.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwg.goupon.R;
import com.mwg.goupon.constant.BusinessBean;
import com.mwg.goupon.util.HttpUtil;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mwg on 2018/1/29.
 */

public class BusinessAdapter extends MyBaseAdapter<BusinessBean.Business> {

    public BusinessAdapter(Context context, List<BusinessBean.Business> datas) {
        super(context, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BusinessAdapter.ViewHolder vh;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.inflate_businessitem_layout, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        BusinessBean.Business item = getItem(position);

        //1、设置图片
        HttpUtil.loadImage(item.getPhoto_url(), vh.iv_pic);

        //2、设置商户名称（有分店名称需设置分店名称）
        String name = item.getName().substring(0, item.getName().indexOf("("));
        if (!TextUtils.isEmpty(item.getBranch_name())) {
            name = name + "(" + item.getBranch_name() + ")";
            Log.d("TAG:",name);
        }
        vh.tv_item_name.setText(name);

        //3、设置用户评分的图片（用一个随机数设置）
        int[] rating = new int[]{R.drawable.movie_star10,
                R.drawable.movie_star20,
                R.drawable.movie_star30,
                R.drawable.movie_star35,
                R.drawable.movie_star40,
                R.drawable.movie_star45,
                R.drawable.movie_star50};
        Random random = new Random();
        int idx = random.nextInt(7);
        vh.iv_item_rating.setImageResource(rating[idx]);

        //4、设置人均消费的价格（也是随机数）
        int price = random.nextInt(100) + 50;
        vh.tv_item_item_avg_price.setText("￥" + price + "/人");

        //5、设置商户所在位置
        int i = random.nextInt(item.getRegions().size());
        vh.tv_item_region.setText(item.getRegions().get(i));

        //6、设置商品类别
        int j = random.nextInt(item.getCategories().size());
        vh.tv_item_category.setText(item.getCategories().get(j));
        //7、设置商户所在的大约距离
        //vh.tv_item_distance.setText("150m");

        return convertView;
    }

    //ViewHolder类的作用——减少不必要的FindViewById——把对底下的控件引用（即自定义的一个对象）
    // 存放在ViewHolder里面，再用View的setTag(holder)方法，把引用与View里以id作为标识的各控件相关联，
    // 以方便在Java代码中用这些引用来对底下各控件进行各种操作。
    public class ViewHolder {
        //利用Butter knife赋值
        @BindView(R.id.iv_business_item)
        ImageView iv_pic;
        @BindView(R.id.tv_business_item_name)
        TextView tv_item_name;
        @BindView(R.id.iv_business_item_rating)
        ImageView iv_item_rating;
        @BindView(R.id.tv_business_item_avg_price)
        TextView tv_item_item_avg_price;
        @BindView(R.id.tv_business_item_region)
        TextView tv_item_region;
        @BindView(R.id.tv_business_item_category)
        TextView tv_item_category;
        @BindView(R.id.tv_business_item_distance)
        TextView tv_item_distance;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
