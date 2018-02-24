package com.mwg.goupon.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mwg.goupon.R;
import com.mwg.goupon.bean.Comment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mwg on 2018/2/8.
 */

public class CommentAdapter extends MyBaseAdapter{
    public CommentAdapter(Context context, List datas) {
        super(context, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView=inflater.inflate(R.layout.inflate_business_comment_layout,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //设置holder里的内容
        Comment comment = (Comment) getItem(position);
        viewHolder.tvNiCheng.setText(comment.getName());
        viewHolder.tvDate.setText(comment.getDate());
        viewHolder.tvContent.setText(comment.getContent());

        return convertView;
    }

    public class ViewHolder{

        @BindView(R.id.tv_comment_nicheng)
        TextView tvNiCheng;
        @BindView(R.id.tv_comment_date)
        TextView tvDate;
        @BindView(R.id.tv_comment_content)
        TextView tvContent;

        public ViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}
