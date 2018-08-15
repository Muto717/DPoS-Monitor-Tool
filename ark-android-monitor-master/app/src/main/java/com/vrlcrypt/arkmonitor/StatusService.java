package com.vrlcrypt.arkmonitor;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Pair;
import android.view.View;

import com.vrlcrypt.StatusServiceNotification;
import com.vrlcrypt.arkmonitor.adapters.viewModel.SettingViewModel;
import com.vrlcrypt.arkmonitor.models.ServerSetting;
import com.vrlcrypt.arkmonitor.models.Status;
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

    private List<ServerSetting> mServerSettings;

    private int notificationIds = 1002243;

    private Consumer<List<ServerSetting>> mServerConsumer = serverSetting -> {
        for (ServerSetting setting : serverSetting) {
            if (!statusPool.containsDelegate(setting)) {
                statusPool.insertDelegate(setting);
            }

            mServerSettings.clear();
            mServerSettings.addAll(serverSetting);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(NOTIFICATION_ID, StatusServiceNotification.getNotification(getApplicationContext(), new ArrayList<>()));

        mServerSettings = new ArrayList<>();

        statusPool = DelegateStatusPool.getInstance();

        mDatabaseSubcription = SettingsDatabase.getInstance(getApplicationContext()).settingDao().getSettings()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mServerConsumer);

        statusPool.mStatusPublisher.subscribe(pairs -> {
            notificationIds++;
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            for (Pair<Integer, Integer> pair : pairs) {
                for (ServerSetting serverSetting : mServerSettings) {
                    if (pair.first.equals(serverSetting.getUId()) && pair.second.equals(Status.MISSED_AWAITING_SLOT) || pair.second.equals(Status.MISSING) || pair.second.equals(Status.NOT_FORGING)) {
                        notificationManager.notify(notificationIds, StatusServiceNotification.statusUpdate(getApplicationContext(), serverSetting, pair));
                        break;
                    }
                }
            }
        });

    }

}
