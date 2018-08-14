package com.vrlcrypt.arkmonitor.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.thorcom.testapp.subscription.SubscriptionManager;
import com.vrlcrypt.arkmonitor.MainActivity;
import com.vrlcrypt.arkmonitor.R;
import com.vrlcrypt.arkmonitor.adapters.MiniServerAdapter;
import com.vrlcrypt.arkmonitor.adapters.viewModel.ServerViewModel;
import com.vrlcrypt.arkmonitor.databinding.FragmentSettingsBinding;
import com.vrlcrypt.arkmonitor.models.ServerSetting;
import com.vrlcrypt.arkmonitor.persistance.SettingsDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SettingsV2Fragment extends Fragment implements OnClickListener {

    private MiniServerAdapter mServerAdapter;

    private FragmentSettingsBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);

        mServerAdapter = new MiniServerAdapter();

        mBinding.listSetting.setAdapter(mServerAdapter);
        mBinding.setOnClick(this);

        loadServers();

        return mBinding.getRoot();
    }

    private void loadServers() {
        SubscriptionManager.getInstance().putSubscription(
                SettingsV2Fragment.class.getSimpleName(),
                SettingsDatabase.getInstance(getContext()).settingDao().getSettings().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .map(serverSettings -> {
                            List<ServerViewModel> toAdd = new ArrayList<>();

                            for (ServerSetting serverSetting : serverSettings) {
                                toAdd.add(new ServerViewModel(serverSetting));
                            }

                            return toAdd;
                        })
                        .subscribe(settings -> {
                            mServerAdapter.setData(settings);
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
                ((MainActivity) getActivity()).showFragment(new AddServerFragment());
                break;
            }
        }

    }

}