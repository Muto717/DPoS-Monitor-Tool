package com.vrlcrypt.arkmonitor.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.vrlcrypt.arkmonitor.fragments.home.HomeServerSettingFragment;
import com.vrlcrypt.arkmonitor.models.ServerSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Young on 06/05/2018.
 */

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<ServerSetting> mDataSource;

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
        return HomeServerSettingFragment.newInstance(mDataSource.get(position));
    }

    public void insert(ServerSetting serverSetting) {
        mDataSource.add(serverSetting);
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mDataSource.isEmpty())
            return "";

        return mDataSource.get(position).getServerName();
    }

}
