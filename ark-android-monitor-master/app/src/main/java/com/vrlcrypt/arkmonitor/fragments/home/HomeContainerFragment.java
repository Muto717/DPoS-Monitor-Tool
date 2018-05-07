package com.vrlcrypt.arkmonitor.fragments.home;

import com.vrlcrypt.arkmonitor.adapters.BaseServerContainerViewPager;
import com.vrlcrypt.arkmonitor.fragments.base.BaseServerContainer;

public class HomeContainerFragment extends BaseServerContainer {

    @Override
    public int getPageType() {
        return BaseServerContainerViewPager.HOME_FRAGMENT;
    }

    @Override
    public String subscriptionTag() {
        return HomeContainerFragment.class.getSimpleName();
    }

}
