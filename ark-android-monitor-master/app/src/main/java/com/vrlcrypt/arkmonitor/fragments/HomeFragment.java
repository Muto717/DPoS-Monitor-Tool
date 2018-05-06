package com.vrlcrypt.arkmonitor.fragments;

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
import com.vrlcrypt.arkmonitor.adapters.MainViewPagerAdapter;
import com.vrlcrypt.arkmonitor.databinding.FragmentMainV2Binding;
import com.vrlcrypt.arkmonitor.fragments.viewModel.MainFragmentViewModel;
import com.vrlcrypt.arkmonitor.models.ServerSetting;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private FragmentMainV2Binding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_v2, container, false);

        mBinding.setViewModel(ViewModelProviders.of(this).get(MainFragmentViewModel.class));

        setupViewPager(mBinding);

        subscribeToServerSettings();

        return mBinding.getRoot();
    }

    private void setupViewPager (FragmentMainV2Binding binding) {
        binding.viewpager.setAdapter(new MainViewPagerAdapter(getChildFragmentManager()));
        binding.tabLayout.addOnTabSelectedListener(this);
    }

    private void subscribeToViews(FragmentMainV2Binding binding) {
        SubscriptionManager.getInstance().putSubscription(
                HomeFragment.class.getSimpleName(),
                RxView.clicks(binding.btnAddNewServer).subscribe(o -> ((MainActivity) getActivity()).showFragment(new SettingsV2Fragment())),
                true
        );
    }

    private void subscribeToServerSettings() {
        mBinding.tabLayout.removeAllTabs();
        SubscriptionManager.getInstance().putSubscription(HomeFragment.class.getSimpleName(),
                mBinding.getViewModel().serverSettingObserver
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::setupServer),
        false);
    }

    @Override
    public void onStart() {
        super.onStart();

        subscribeToViews(mBinding);
        subscribeToServerSettings();
    }

    @Override
    public void onStop() {
        super.onStop();

        SubscriptionManager.getInstance().dispose(HomeFragment.class.getSimpleName());
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
        tabLayout.addTab(tabLayout.newTab().setText(serverSetting.getUsername()));
    }

    private void addPage (ViewPager viewPager, ServerSetting serverSetting) {
        ((MainViewPagerAdapter) viewPager.getAdapter()).insert(HomeServerSettingFragment.newInstance(serverSetting));
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mBinding.viewpager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

}
