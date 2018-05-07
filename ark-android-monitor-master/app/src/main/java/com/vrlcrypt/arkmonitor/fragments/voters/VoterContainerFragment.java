package com.vrlcrypt.arkmonitor.fragments.voters;

import com.vrlcrypt.arkmonitor.adapters.BaseServerContainerViewPager;
import com.vrlcrypt.arkmonitor.fragments.base.BaseServerContainer;

public class VoterContainerFragment extends BaseServerContainer {

    @Override
    public int getPageType() {
        return BaseServerContainerViewPager.VOTERS_FRAGMENT;
    }

    @Override
    public String subscriptionTag() {
        return VoterContainerFragment.class.getSimpleName();
    }

}
