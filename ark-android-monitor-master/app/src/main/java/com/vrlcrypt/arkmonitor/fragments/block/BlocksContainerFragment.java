package com.vrlcrypt.arkmonitor.fragments.block;

import com.vrlcrypt.arkmonitor.adapters.BaseServerContainerViewPager;
import com.vrlcrypt.arkmonitor.fragments.base.BaseServerContainer;

/**
 * Created by James Young on 07/05/2018.
 */

public class BlocksContainerFragment extends BaseServerContainer {

    @Override
    public int getPageType() {
        return BaseServerContainerViewPager.BLOCK_FRAGMENT;
    }

    @Override
    public String subscriptionTag() {
        return BlocksContainerFragment.class.getSimpleName();
    }

}
