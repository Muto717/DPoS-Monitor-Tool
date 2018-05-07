package com.vrlcrypt.arkmonitor.fragments.delegates;

import com.vrlcrypt.arkmonitor.adapters.BaseServerContainerViewPager;
import com.vrlcrypt.arkmonitor.fragments.base.BaseServerContainer;

public class DelegateContainerFragment extends BaseServerContainer {

    @Override
    public int getPageType() {
        return BaseServerContainerViewPager.DELEGATES_FRAGMENT;
    }

    @Override
    public String subscriptionTag() {
        return DelegateContainerFragment.class.getSimpleName();
    }

}
