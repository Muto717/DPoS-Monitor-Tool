package com.vrlcrypt.arkmonitor.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.vrlcrypt.arkmonitor.fragments.HomeServerSettingFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Young on 06/05/2018.
 */

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<HomeServerSettingFragment> mDataSource;

    public MainViewPagerAdapter (FragmentManager fragmentManager) {
        super(fragmentManager);

        this.mDataSource = new ArrayList<>();
    }


    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mDataSource.get(position);
    }

    public void insert(HomeServerSettingFragment fragment) {
        mDataSource.add(fragment);
        notifyDataSetChanged();
    }

}
