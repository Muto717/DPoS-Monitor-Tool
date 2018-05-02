package com.vrlcrypt.arkmonitor.fragments.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;

import com.thorcom.testapp.subscription.SubscriptionManager;
import com.vrlcrypt.arkmonitor.persistance.SettingsDatabase;

import io.reactivex.functions.Consumer;

public class MainFragmentViewModel extends AndroidViewModel {

    public ObservableBoolean hasServerSetup;

    public MainFragmentViewModel(@NonNull Application application) {
        super(application);

        hasServerSetup = new ObservableBoolean();

        SettingsDatabase.getInstance(application).getCount().subscribe(integer -> {
            if (integer > 0) hasServerSetup.set(true);
        }, throwable -> hasServerSetup.set(false));
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        SubscriptionManager.getInstance().
    }
}
