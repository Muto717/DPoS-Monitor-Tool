package com.vrlcrypt.arkmonitor.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.vrlcrypt.arkmonitor.fragments.delegates.DelegatesFragment;
import com.vrlcrypt.arkmonitor.fragments.peers.PeersFragment;
import com.vrlcrypt.arkmonitor.fragments.transactions.LatestTransactionsFragment;
import com.vrlcrypt.arkmonitor.fragments.block.BlocksFragment;
import com.vrlcrypt.arkmonitor.fragments.info.HomeServerSettingFragment;
import com.vrlcrypt.arkmonitor.fragments.voters.VotersFragment;
import com.vrlcrypt.arkmonitor.fragments.votes.VotesFragment;
import com.vrlcrypt.arkmonitor.models.ServerSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Young on 06/05/2018.
 */

public class BaseServerContainerViewPager extends FragmentStatePagerAdapter {

    public static final int
            BLOCK_FRAGMENT = 0, HOME_FRAGMENT = 1,
            TRANSACTIONS_FRAGMENT = 2, PEERS_FRAGMENT = 3,
            DELEGATES_FRAGMENT = 4, VOTES_FRAGMENT = 5,
            VOTERS_FRAGMENT = 6;

    private List<ServerSetting> mDataSource;

    private int type;

    public BaseServerContainerViewPager(int type, FragmentManager fragmentManager) {
        super(fragmentManager);

        this.type = type;
        this.mDataSource = new ArrayList<>();
    }


    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Fragment getItem(int position) {
        switch (type) {
            case BLOCK_FRAGMENT:
                return BlocksFragment.newInstance(mDataSource.get(position));
            case HOME_FRAGMENT:
                return HomeServerSettingFragment.newInstance(mDataSource.get(position));
            case TRANSACTIONS_FRAGMENT:
                return LatestTransactionsFragment.newInstance(mDataSource.get(position));
            case PEERS_FRAGMENT:
                return PeersFragment.newInstance(mDataSource.get(position));
            case DELEGATES_FRAGMENT:
                return DelegatesFragment.newInstance(mDataSource.get(position));
            case VOTES_FRAGMENT:
                return VotesFragment.newInstance(mDataSource.get(position));
            case VOTERS_FRAGMENT:
                return VotersFragment.newInstance(mDataSource.get(position));
            default:
                return null;
        }
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
