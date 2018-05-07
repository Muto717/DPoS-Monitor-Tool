package com.vrlcrypt.arkmonitor.fragments.base;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.thorcom.testapp.subscription.SubscriptionManager;
import com.vrlcrypt.arkmonitor.MainActivity;
import com.vrlcrypt.arkmonitor.R;
import com.vrlcrypt.arkmonitor.adapters.BaseServerContainerViewPager;
import com.vrlcrypt.arkmonitor.databinding.FragmentBaseServerContainerBinding;
import com.vrlcrypt.arkmonitor.models.ServerSetting;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by James Young on 07/05/2018.
 */

public abstract class BaseServerContainer extends Fragment {

    private FragmentBaseServerContainerBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_server_container, container, false);

        mBinding.setViewModel(ViewModelProviders.of(this).get(BaseServerContainerViewModel.class));

        setupViewPager(mBinding);

        subscribeToServerSettings();

        return mBinding.getRoot();
    }

    private void setupViewPager (FragmentBaseServerContainerBinding binding) {
        binding.viewpager.setAdapter(new BaseServerContainerViewPager(getPageType(), getChildFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewpager);
    }

    private void subscribeToViews(FragmentBaseServerContainerBinding binding) {
        SubscriptionManager.getInstance().putSubscription(
                subscriptionTag(),
                RxView.clicks(binding.btnAddNewServer).subscribe(o -> ((MainActivity) getActivity()).selectMenuItem(R.id.nav_settings)),
                true
        );
    }

    private void subscribeToServerSettings() {
        mBinding.tabLayout.removeAllTabs();

        SubscriptionManager.getInstance().putSubscription(subscriptionTag(),
                mBinding.getViewModel().serverSettingObserver
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::setupServer),
                false);
    }

    @Override
    public void onStart() {
        super.onStart();

        subscribeToViews(mBinding);
    }

    @Override
    public void onStop() {
        super.onStop();

        SubscriptionManager.getInstance().dispose(subscriptionTag());
    }

    private void setupServer (List<ServerSetting> serverSettings) {
        TabLayout tabLayout = mBinding.tabLayout;
        ViewPager viewPager = mBinding.viewpager;

        for (ServerSetting serverSetting : serverSettings) {
            addTab(tabLayout, serverSetting);
            addPage(viewPager, serverSetting);
        }
    }

    private void addTab (TabLayout tabLayout, ServerSetting serverSetting) {
        tabLayout.addTab(tabLayout.newTab().setText(serverSetting.getServerName()));
    }

    private void addPage (ViewPager viewPager, ServerSetting serverSetting) {
        ((BaseServerContainerViewPager) viewPager.getAdapter()).insert(serverSetting);
    }

    public abstract int getPageType ();

    public abstract String subscriptionTag();

}
