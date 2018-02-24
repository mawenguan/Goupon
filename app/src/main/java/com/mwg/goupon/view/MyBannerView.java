package com.mwg.goupon.view;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mwg.goupon.R;

/**
 * Created by mwg on 2018/1/31.
 * <p>
 * 这是一个自定义的VIEW
 */

public class MyBannerView extends FrameLayout {

    ViewPager viewPager;
    ImageView ivClose;
    LinearLayout llIndicator;
    PagerAdapter adapter;

    //ViewPager管理的所有图片（数量为用户所能看到的图片的数量+2——即多添加一个首张喝末尾一张）
    int[] resIds;

    Handler handler = new Handler();
    boolean flag;

    OnCloseBannerListener listener;

    public MyBannerView(@NonNull Context context, int[] ids) {
        super(context);
        if (ids != null && ids.length > 0) {
            //创建MyBannerView时，创建者传入了要显示的轮播图片
            this.resIds = new int[ids.length + 2];
            this.resIds[0] = ids[ids.length - 1];
            this.resIds[this.resIds.length - 1] = ids[0];
            for (int i = 0; i < ids.length; i++) {
                this.resIds[i + 1] = ids[i];
            }
        } else {
            //使用系统默认的轮播图片(banner_1~baner_4)
            resIds = new int[]{R.drawable.banner_4,
                    R.drawable.banner_1,
                    R.drawable.banner_2,
                    R.drawable.banner_3,
                    R.drawable.banner_4,
                    R.drawable.banner_1,};
        }

        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.business_banner_layout, this, false);
        this.addView(view);

        initView(view);
        startView();
    }

    public void setOnCloseBannerListener(OnCloseBannerListener listener) {
        this.listener = listener;
    }

    private void startView() {

        flag = true;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (flag) {
                    int idx = viewPager.getCurrentItem();
                    viewPager.setCurrentItem(idx + 1);
                    handler.postDelayed(this, 3000);
                }
            }
        }, 3000);

    }

    private void stopView() {
        flag = false;
        handler.removeCallbacksAndMessages(null);
    }

    private void initView(View view) {
        viewPager = view.findViewById(R.id.vp_banner);
        ivClose = view.findViewById(R.id.iv_banner_close);
        llIndicator = view.findViewById(R.id.ll_banner_indicator);

        //初始化适配器
        adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return resIds.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {

                ImageView iv = new ImageView(getContext());
                int resId = resIds[position];
                iv.setImageResource(resId);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                container.addView(iv);

                return iv;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        };

        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    viewPager.setCurrentItem(resIds.length - 2, false);
                    setIndicator(llIndicator.getChildCount() - 1);
                } else if (position == resIds.length - 1) {
                    viewPager.setCurrentItem(1, false);
                    setIndicator(0);
                } else {
                    setIndicator(position - 1);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    stopView();
                } else if (action == MotionEvent.ACTION_UP) {
                    startView();
                }
                return false;
            }
        });

        //llIndicator中填充对应的ImageView，作为滑动时的指示器
        for (int i = 0; i < resIds.length - 2; i++) {
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(R.drawable.banner_dot);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    3, getResources().getDisplayMetrics());
            params.setMargins(margin, 0, margin, 0);
            iv.setLayoutParams(params);

            llIndicator.addView(iv);
        }

        setIndicator(0);

        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClose();
                }
            }
        });
    }

    /**
     * 设置谋“指示器”中的图片颜色为橘红色
     *
     * @param idx
     */
    private void setIndicator(int idx) {

        for (int i = 0; i < llIndicator.getChildCount(); i++) {
            ImageView imageView = (ImageView) llIndicator.getChildAt(i);
            if (i == idx) {
                imageView.setImageResource(R.drawable.banner_dot_pressed);
            } else {
                imageView.setImageResource(R.drawable.banner_dot);
            }
        }
    }

    public interface OnCloseBannerListener {
        void onClose();
    }
}
