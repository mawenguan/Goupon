package com.mwg.goupon.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mwg.goupon.R;
import com.mwg.goupon.adapter.CommentAdapter;
import com.mwg.goupon.bean.Comment;
import com.mwg.goupon.constant.BusinessBean;
import com.mwg.goupon.util.HttpUtil;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends Activity {

    BusinessBean.Business business;

    @BindView(R.id.lv_detail_details)
    ListView listView;
    List<Comment> datas;
    CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        business = (BusinessBean.Business) getIntent().
                getSerializableExtra("business");
        Log.d("TAG", "onCreate:DetaiActivity的商户信息： " + business.getReview_list_url());
        ButterKnife.bind(this);
        initListView();
    }

    private void initListView() {
        datas = new ArrayList<Comment>();
        adapter = new CommentAdapter(this,datas);

        LayoutInflater inflater = LayoutInflater.from(this);
        View headerBusiness = inflater.inflate(
                R.layout.inflate_businessitem_layout, listView, false);
        initHeaderBusiness(headerBusiness);
        View headerInfo = inflater.inflate(
                R.layout.header_list_detail_business_info, listView, false);
        initHeaderInfo(headerInfo);
        listView.addHeaderView(headerBusiness);
        listView.addHeaderView(headerInfo);

        listView.setAdapter(adapter);
    }

    private void initHeaderInfo(View view) {
        TextView tvAddress = view.findViewById(R.id.tv_header_list_businessaddress);
        tvAddress.setText(business.getAddress());
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this,MapActivity.class);
                intent.putExtra("business",business);
                intent.putExtra("from","detail");
                startActivity(intent);
            }
        });

        TextView tvTelphone = view.findViewById(R.id.tv_header_list_businesstelephone);
        tvTelphone.setText(business.getTelephone());
    }

    private void initHeaderBusiness(View view) {
        ImageView ivPic = view.findViewById(R.id.iv_business_item);
        ImageView ivRating = view.findViewById(R.id.iv_business_item_rating);
        TextView tvName = view.findViewById(R.id.tv_business_item_name);
        TextView tvPrice = view.findViewById(R.id.tv_business_item_avg_price);
        TextView tvRegion = view.findViewById(R.id.tv_business_item_region);
        TextView tvCategory = view.findViewById(R.id.tv_business_item_category);
        TextView tvDistance = view.findViewById(R.id.tv_business_item_distance);

        HttpUtil.loadImage(business.getPhoto_url(), ivPic);

        String name = business.getName().substring(0, business.getName().indexOf("("));
        if (!TextUtils.isEmpty(business.getBranch_name())) {
            name = name + "(" + business.getBranch_name() + ")";
        }
        tvName.setText(name);

        int[] stars = new int[]{R.drawable.movie_star10,
                R.drawable.movie_star20,
                R.drawable.movie_star30,
                R.drawable.movie_star35,
                R.drawable.movie_star40,
                R.drawable.movie_star45,
                R.drawable.movie_star50};
        Random rand = new Random();
        int idx = rand.nextInt(7);
        ivRating.setImageResource(stars[idx]);

        int price = rand.nextInt(100) + 50;

        tvPrice.setText("￥" + price + "/人");

        //5、设置商户所在位置
        Random random = new Random();
        int i = random.nextInt(business.getRegions().size());
        tvRegion.setText(business.getRegions().get(i));

        //6、设置商品类别
        int j = random.nextInt(business.getCategories().size());
        tvCategory.setText(business.getCategories().get(j));
        //7、设置商户所在的大约距离
        //vh.tv_item_distance.setText("150m");

        tvDistance.setText("150m");
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {

//        datas.add("aaa");
//        datas.add("aaa");
//        datas.add("aaa");
//        datas.add("aaa");
//        datas.add("aaa");
//        adapter.notifyDataSetChanged();

        HttpUtil.getComment(business.getReview_list_url(), new HttpUtil.OnResponseListener<Document>() {
            @Override
            public void onResponse(Document document) {
                //1、解析回调接口中传回来的document对象，得到comments;
                List<Comment> comments = new ArrayList<Comment>();
//                Elements elements = document.select("div[class=comment-list] li[data-id]");
//                for (Element element : elements) {
//                    Comment comment = new Comment();
//                    Element imgElement = element.select("div[class=pic] img").get(0);
//                    comment.setName(imgElement.attr("title"));
//                    comment.setAvatar(imgElement.attr("src"));
//
//                    //解析并设置人均消费
//                    Elements spanElements = element.select("div[class=user-info] span[class=comm-per]");
//                    if (spanElements.size() > 0) {
//                        //人均 ￥85
//                        comment.setPrice(spanElements.get(0).text().split(" ")[1] + "/人");
//                    } else {
//                        comment.setPrice("");
//                    }
//                    //解析并设置评论标题
//                    Element spanElement = element.select("div[class=user-info] span[title]").get(0);
//                    String rate = spanElement.attr("class");
//                    //拿到的是star40
//                    comment.setRating(rate.split("-")[3]);
//
//                    //评论的正文
//                    Element divElement = element.select("div[class=review-words Hide]").get(0);
//                    comment.setContent(divElement.text());
//
//                    //评论中的照片
//                    Elements imgElements = element.select("div[class=shop-photo] img");
//                    int size = imgElements.size();
//                    if (size > 3) {
//                        size = 3;
//                    }
//
//                    String[] imgs = new String[size];
//                    for (int i = 0; i < size; i++) {
//                        imgs[i] = imgElements.get(i).attr("src");
//                        comment.setImgs(imgs);
//                    }
//
//                    //评论中的日期
//                    Element spanEle = element.select("div[class=misc-info] span[class=time]").get(0);
//                    comment.setDate(spanEle.text());
//
//                    //内容添加完毕，提交
//                    comments.add(comment);
//                }
                Elements elements = document.select("div[class=reviews-items]");
                for (Element element : elements) {
                    Comment comment = new Comment();

                    //评论者得昵称
                    Element nameElement = element.select("div[class=dper-info]").get(0);
                    comment.setName(nameElement.text());


                    //评论中的内容
                    Element divElement = element.select("div[class=review-words Hide]").get(0);
                    comment.setContent(divElement.text());


                    //评论中的日期
                    Element spanElement = element.select("div[class=misc-info clearfix] span[class=time]").get(0);
                    Log.d("TAG:", "中间内容：" + spanElement);
                    comment.setDate(spanElement.text());

                    //内容添加完毕，提交
                    comments.add(comment);
                }
                Log.d("TAG:", "评论内容：" + comments);

                //2、放到ListView中呈现
                adapter.addAll(comments,true);
            }
        });
    }
}
