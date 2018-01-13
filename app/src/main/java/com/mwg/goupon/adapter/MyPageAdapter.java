package com.mwg.goupon.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mwg.goupon.fragment.FragmentA;
import com.mwg.goupon.fragment.FragmentB;
import com.mwg.goupon.fragment.FragmentC;
import com.mwg.goupon.fragment.FragmentD;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mwg on 2018/1/11.
 */

public class MyPageAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments;

    public MyPageAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<Fragment>();
        fragments.add(new FragmentA());
        fragments.add(new FragmentB());
        fragments.add(new FragmentC());
        fragments.add(new FragmentD());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
