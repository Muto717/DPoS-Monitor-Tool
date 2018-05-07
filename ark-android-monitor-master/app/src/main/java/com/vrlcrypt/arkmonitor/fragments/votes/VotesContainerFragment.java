package com.vrlcrypt.arkmonitor.fragments.votes;

import com.vrlcrypt.arkmonitor.adapters.BaseServerContainerViewPager;
import com.vrlcrypt.arkmonitor.fragments.base.BaseServerContainer;

public class VotesContainerFragment extends BaseServerContainer {

    @Override
    public int getPageType() {
        return BaseServerContainerViewPager.VOTES_FRAGMENT;
    }

    @Override
    public String subscriptionTag() {
        return VotesContainerFragment.class.getSimpleName();
    }

}
