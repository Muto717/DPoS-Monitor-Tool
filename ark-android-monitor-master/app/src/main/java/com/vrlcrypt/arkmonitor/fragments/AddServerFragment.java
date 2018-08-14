package com.vrlcrypt.arkmonitor.fragments;

import android.app.AlarmManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.thorcom.testapp.subscription.SubscriptionManager;
import com.vrlcrypt.arkmonitor.MainActivity;
import com.vrlcrypt.arkmonitor.R;
import com.vrlcrypt.arkmonitor.ViewUtil;
import com.vrlcrypt.arkmonitor.adapters.ServerAdapterSettingList;
import com.vrlcrypt.arkmonitor.adapters.viewModel.SettingViewModel;
import com.vrlcrypt.arkmonitor.databinding.FragmentSettingsBinding;
import com.vrlcrypt.arkmonitor.models.Server;
import com.vrlcrypt.arkmonitor.models.ServerSetting;
import com.vrlcrypt.arkmonitor.persistance.SettingsDatabase;
import com.vrlcrypt.arkmonitor.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class AddServerFragment extends Fragment implements OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_add_server, container, false);

        view.findViewById(R.id.btn_save).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);

        ArrayAdapter<String> serverStringAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, Server.getServers());
        serverStringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ((Spinner) view.findViewById(R.id.servers)).setAdapter(serverStringAdapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                ServerSetting serverSetting = new ServerSetting();

                serverSetting.setServerName(ViewUtils.getEditTextValue(getView(), R.id.fld_server_name, ""));
                serverSetting.setArkAddress(ViewUtils.getEditTextValue(getView(), R.id.fld_address, ""));
                serverSetting.setPublicKey(ViewUtils.getEditTextValue(getView(), R.id.fld_public_key, ""));
                serverSetting.setIpAddress(ViewUtils.getEditTextValue(getView(), R.id.fld_public_key, "0.0.0.0"));
                serverSetting.setPort(Integer.valueOf(ViewUtils.getEditTextValue(getView(), R.id.port, "4001")));
                serverSetting.setSslEnabled(((CheckBox) getView().findViewById(R.id.ssl_enabled)).isChecked());
                serverSetting.setServer(Server.fromId(((Spinner) getView().findViewById(R.id.servers)).getSelectedItemPosition()));
                serverSetting.setNotificationInterval(getInterval(((Spinner) getView().findViewById(R.id.notification_interval)).getSelectedItemPosition()));

                SettingsDatabase.getInstance(getContext())
                        .insert(serverSetting)
                        .doOnComplete(() -> ((MainActivity) getActivity()).showFragment(new SettingsV2Fragment()))
                        .subscribe();
                break;
            case R.id.btn_cancel:
                ((MainActivity) getActivity()).showFragment(new SettingsV2Fragment());
                break;
        }
    }

    private long getInterval(int position) {
        switch (position) {
            case 1:
                return  AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            case 2:
                return AlarmManager.INTERVAL_HALF_HOUR;
            default:
                return 420000L;
        }
    }

}