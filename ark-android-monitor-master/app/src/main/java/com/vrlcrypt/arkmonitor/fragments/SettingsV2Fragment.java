package com.vrlcrypt.arkmonitor.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.thorcom.testapp.subscription.SubscriptionManager;
import com.vrlcrypt.arkmonitor.R;
import com.vrlcrypt.arkmonitor.adapters.ServerAdapterSettingList;
import com.vrlcrypt.arkmonitor.adapters.viewModel.SettingViewModel;
import com.vrlcrypt.arkmonitor.databinding.FragmentSettingsBinding;
import com.vrlcrypt.arkmonitor.models.ServerSetting;
import com.vrlcrypt.arkmonitor.persistance.SettingsDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class SettingsV2Fragment extends Fragment implements OnClickListener {

    private ServerAdapterSettingList mSettingAdapter;
    private FragmentSettingsBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);

        mSettingAdapter = new ServerAdapterSettingList();

        mBinding.listSetting.setAdapter(mSettingAdapter);
        mBinding.setOnClick(this);

        loadServers();

        return mBinding.getRoot();
    }

    private void loadServers() {
        SubscriptionManager.getInstance().putSubscription(
                SettingsV2Fragment.class.getSimpleName(),
                SettingsDatabase.getInstance(getContext()).settingDao().getSettings().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .map(serverSettings -> {
                            List<SettingViewModel> toAdd = new ArrayList<>();

                            for (ServerSetting serverSetting : serverSettings) {
                                boolean found = false;

                                for (SettingViewModel viewModel : mSettingAdapter.getDataSource())
                                    if (viewModel.getServerSetting().getServerName() != null)
                                        if (viewModel.getServerSetting().getServerName().equals(serverSetting.getServerName()))
                                            found = true;

                                if (!found)
                                    toAdd.add(new SettingViewModel(getActivity().getApplication(), serverSetting));
                            }

                            return toAdd;
                        })
                        .subscribe(settings -> {
                            for (SettingViewModel viewModel : settings)
                                mSettingAdapter.insertNew(viewModel);

                            if (mSettingAdapter.getDataSource().isEmpty())
                                mBinding.txtNoServers.setVisibility(View.VISIBLE);
                            else {
                                mBinding.txtNoServers.setVisibility(View.GONE);
                                mBinding.listSetting.scrollToPosition(0);
                            }
                        }),
                true);
    }

    @Override
    public void onDestroy() {
        SubscriptionManager.getInstance().dispose(SettingsV2Fragment.class.getSimpleName());

        super.onDestroy();
    }

    @Override
    public void onClick(final View v) {

        switch (v.getId()) {
            case R.id.btn_add_new_server: {

                SettingsDatabase.getInstance(getContext()).insert(new ServerSetting())
                        .doOnComplete(() -> mBinding.listSetting.scrollToPosition(0))
                        .subscribe();

                break;
            }
        }

    }

}