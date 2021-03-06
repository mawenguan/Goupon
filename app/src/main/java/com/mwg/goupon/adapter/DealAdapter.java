package com.mwg.goupon.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwg.goupon.R;
import com.mwg.goupon.bean.TuanBean;
import com.mwg.goupon.util.HttpUtil;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mwg on 2018/1/16.
 */

public class DealAdapter extends MyBaseAdapter<TuanBean.Deal> {

    public DealAdapter(Context context, List<TuanBean.Deal> datas) {
        super(context, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.inflate_item_deal_layout, parent, false);
            viewHolder = new ViewHolder(convertView);
//            viewHolder.ivPic = convertView.findViewById(R.id.iv_item_deal_image);
//            viewHolder.tvTitle = convertView.findViewById(R.id.tv_item_deal_name);
//            viewHolder.tvDetail = convertView.findViewById(R.id.tv_item_deal_detail);
//            viewHolder.tvDistance = convertView.findViewById(R.id.tv_item_deal_distance);
//            viewHolder.tvPrice = convertView.findViewById(R.id.tv_item_deal_price);
//            viewHolder.tvCount = convertView.findViewById(R.id.tv_item_deal_price);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TuanBean.Deal deal = getItem(position);

        //呈现图片
        HttpUtil.displayImage(deal.getS_image_url(), viewHolder.ivPic);

        viewHolder.tvTitle.setText(deal.getTitle());
        viewHolder.tvDetail.setText(deal.getDescription());
        viewHolder.tvPrice.setText(deal.getCurrent_price() + "");
        Random random = new Random();
        int count = random.nextInt(2000) + 500;
        viewHolder.tvCount.setText("已售：" + count);
        //TODO 距离 viewHolder.tvDistance.setText(“XXXX”);
        return convertView;
    }

    public class ViewHolder {
        @BindView(R.id.iv_item_deal_image)
        ImageView ivPic;
        @BindView(R.id.tv_item_deal_name)
        TextView tvTitle;
        @BindView(R.id.tv_item_deal_detail)
        TextView tvDetail;
        @BindView(R.id.tv_item_deal_distance)
        TextView tvDistance;
        @BindView(R.id.tv_item_deal_price)
        TextView tvPrice;
        @BindView(R.id.tv_item_deal_sellcount)
        TextView tvCount;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
