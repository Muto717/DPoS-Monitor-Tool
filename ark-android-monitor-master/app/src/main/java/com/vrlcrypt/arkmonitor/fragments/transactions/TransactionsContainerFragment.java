package com.vrlcrypt.arkmonitor.fragments.transactions;

import com.vrlcrypt.arkmonitor.adapters.BaseServerContainerViewPager;
import com.vrlcrypt.arkmonitor.fragments.base.BaseServerContainer;

public class TransactionsContainerFragment extends BaseServerContainer {

    @Override
    public int getPageType() {
        return BaseServerContainerViewPager.TRANSACTIONS_FRAGMENT;
    }

    @Override
    public String subscriptionTag() {
        return TransactionsContainerFragment.class.getSimpleName();
    }

}
