/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library.internal;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.handmark.pulltorefresh.library.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.util.Log;
import android.view.Gravity;

public class MyLoadingLayout extends LoadingLayout {

    private int[] resIds = new int[]{
            R.drawable.dropdown_anim_00,
            R.drawable.dropdown_anim_01,
            R.drawable.dropdown_anim_02,
            R.drawable.dropdown_anim_03,
            R.drawable.dropdown_anim_04,
            R.drawable.dropdown_anim_05,
            R.drawable.dropdown_anim_06,
            R.drawable.dropdown_anim_07,
            R.drawable.dropdown_anim_08,
            R.drawable.dropdown_anim_09,
            R.drawable.dropdown_anim_10
    };

    public MyLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);

    }

    public void onLoadingDrawableSet(Drawable imageDrawable) {
    }

    protected void onPullImpl(float scaleOfLayout) {
        if (scaleOfLayout <= 1) {
            int idx = (int) Math.ceil(scaleOfLayout * 10);
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resIds[idx]);
			bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()*idx/10,bitmap.getHeight()*idx/10,true);
            mHeaderImage.setImageBitmap(bitmap);
        } else {
            mHeaderImage.setImageResource(resIds[10]);

        }
    }

    @Override
    protected void refreshingImpl() {
        Drawable drawable = getResources().getDrawable(R.drawable.refreshing_anim);
        mHeaderImage.setImageDrawable(drawable);
        ((AnimationDrawable) drawable).start();
    }

    @Override
    protected void resetImpl() {
    }

    @Override
    protected void pullToRefreshImpl() {
        // NO-OP
    }

    @Override
    protected void releaseToRefreshImpl() {
        // NO-OP
    }

    @Override
    protected int getDefaultDrawableResId() {
        return R.drawable.dropdown_anim_00;
    }

}
