package com.vrlcrypt.arkmonitor.fragments.peers;

import com.vrlcrypt.arkmonitor.adapters.BaseServerContainerViewPager;
import com.vrlcrypt.arkmonitor.fragments.base.BaseServerContainer;

public class PeersContainerFragment extends BaseServerContainer {

    @Override
    public int getPageType() {
        return BaseServerContainerViewPager.PEERS_FRAGMENT;
    }

    @Override
    public String subscriptionTag() {
        return PeersContainerFragment.class.getSimpleName();
    }

}
