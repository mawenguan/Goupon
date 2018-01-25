package com.mwg.goupon.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.mwg.goupon.R;

import java.lang.reflect.Type;

/**
 * Created by mwg on 2018/1/25.
 */

public class MyLetterView extends View {

    private String[] letters = {"热门", "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z"};

    Paint paint;

    OnTouchLetterListener listener;

    //声明自定义属性
    int letterColor;

    public MyLetterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyLetterView);
        letterColor = typedArray.getColor(R.styleable.MyLetterView_letter_color, Color.BLACK);
        typedArray.recycle();

        initPaint();
     }

    public void setOnTouchLetterListener(OnTouchLetterListener listener) {
        this.listener = listener;
    }

    /**
     * 画笔的初始化
     */
    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //使用画笔画出来的文字的大小
        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                14, getResources().getDisplayMetrics());
        paint.setTextSize(size);
        paint.setColor(letterColor);
    }

    /**
     * 测量当前view的尺寸（即宽和高），可重写也可不重写
     * 回头重写——针对当自定义View属性设置为"wrap_content"时的需求
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //改写时一定要保留View的onMeasure方法调用
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //只针对当进行"wrap_content"属性设置时才需要改写
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (mode==MeasureSpec.AT_MOST){
            int leftPadding = getPaddingLeft();
            int rightPadding = getPaddingRight();
            int contentWidth = 0;
            for (int i=0;i<letters.length;i++){
                String letter = letters[i];

                Rect bounds = new Rect();
                paint.getTextBounds(letter,0,letter.length(),bounds);

                if (bounds.width()>contentWidth){
                    contentWidth=bounds.width();
                }
            }
            int size = leftPadding + contentWidth+ rightPadding;
            setMeasuredDimension(size,MeasureSpec.getSize(heightMeasureSpec));
        }
    }

//    /**
//     * 决定子View在当前View中的布局（因为自定义中除了27个字符串外，
// 没有任何的子view，因此该方法在MyLetterView中不会用到，也不需要重写）
//     * @param changed
//     * @param left
//     * @param top
//     * @param right
//     * @param bottom
//     */
//
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//    }

    /**
     * 一定要重写——即将定义的内容写到画布（canvas）上去
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //整个控件（MyLetterView）的宽度
        int width = getWidth();
        //整个控件（MyLetterView）的高度
        int height = getHeight();

        for (int i = 0; i < letters.length; i++) {
            String letter = letters[i];

            //获取文字边界的大小
            Rect bounds = new Rect();
            paint.getTextBounds(letter, 0, letter.length(), bounds);
            int textWidth = bounds.width();
            int textHeight = bounds.height();

            //分配给letter小空间的宽度的一半减去letter本身宽度的一半
            float x = width / 2 - textWidth / 2;
            //分配给letter小空间的高度的一半加上letter本身高度的一半
            float y = height / letters.length / 2 + textHeight / 2 + i * height / letters.length;

            canvas.drawText(letter, x, y, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //按下，移动，还是抬起
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                //改变背景
                if (listener!=null){
                    //手指按下或者移动时距离MyLetterView顶部的距离
                    float y = event.getY();
                    //根据距离y换算出所指向位置的字母在数组中的下标值
                    int idx = (int) ((y/getHeight())*letters.length);
                    if (idx>=0&&idx<letters.length){
                        String letter = letters[idx];
                        listener.onTouchLetter(this,letter);
                    }
                }
                setBackgroundColor(Color.GRAY);
                break;
            default:
                setBackgroundColor(Color.TRANSPARENT);
                break;
        }
        return true;
    }

    public interface OnTouchLetterListener {
        void onTouchLetter(MyLetterView view, String letter);
    }
}
