package com.vrlcrypt.arkmonitor;

import android.util.Pair;
import android.view.View;

import com.vrlcrypt.StatusServiceNotification;
import com.vrlcrypt.arkmonitor.adapters.viewModel.SettingViewModel;
import com.vrlcrypt.arkmonitor.models.ServerSetting;
import com.vrlcrypt.arkmonitor.persistance.SettingsDatabase;
import com.vrlcrypt.arkmonitor.services.DelegateStatusPool;
import com.vrlcrypt.arkmonitor.utils.BindableService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StatusService extends BindableService {

    private static final int NOTIFICATION_ID = 1001;

    private DelegateStatusPool statusPool;

    private Disposable mDatabaseSubcription;

    private Consumer<List<ServerSetting>> mServerConsumer = serverSetting -> {
        for (ServerSetting setting : serverSetting) {
            if (!statusPool.containsDelegate(setting.getServerName())) {
                statusPool.insertDelegate(setting);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(NOTIFICATION_ID, StatusServiceNotification.getNotification(getApplicationContext(), new ArrayList<>()));

        statusPool = DelegateStatusPool.getInstance();

        mDatabaseSubcription = SettingsDatabase.getInstance(getApplicationContext()).settingDao().getSettings()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mServerConsumer);

        statusPool.mStatusPublisher.subscribe(pairs -> startForeground(NOTIFICATION_ID, StatusServiceNotification.getNotification(getApplicationContext(), pairs)));
    }

}
