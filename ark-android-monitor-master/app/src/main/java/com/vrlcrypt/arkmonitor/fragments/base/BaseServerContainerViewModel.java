package com.vrlcrypt.arkmonitor.fragments.base;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;

import com.thorcom.testapp.subscription.SubscriptionManager;
import com.vrlcrypt.arkmonitor.models.ServerSetting;
import com.vrlcrypt.arkmonitor.persistance.SettingsDatabase;

import java.util.List;

import io.reactivex.Flowable;

public class BaseServerContainerViewModel extends AndroidViewModel {

    public ObservableBoolean hasServerSetup;

    public Flowable<List<ServerSetting>> serverSettingObserver = SettingsDatabase.getInstance(getApplication()).settingDao().getSettings();

    public BaseServerContainerViewModel(@NonNull Application application) {
        super(application);

        hasServerSetup = new ObservableBoolean();

        SubscriptionManager.getInstance().putSubscription(BaseServerContainerViewModel.class.getSimpleName(),
                serverSettingObserver.subscribe(serverSettings -> {
                    if (serverSettings.isEmpty())
                        hasServerSetup.set(false);
                    else
                        hasServerSetup.set(true);
                }), false
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        SubscriptionManager.getInstance().dispose(BaseServerContainerViewModel.class.getSimpleName());
    }

}
