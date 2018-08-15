package com.vrlcrypt.arkmonitor.adapters.viewModel;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;

import com.vrlcrypt.arkmonitor.models.ServerSetting;

public class ServerViewModel extends ViewModel {

    private ServerSetting mServer;

    public ObservableField<Integer> currentStatus;

    public ServerViewModel(ServerSetting mServer) {
        this.mServer = mServer;
        currentStatus = new ObservableField<>(-1);
    }

    public String getAddress( ) {
        return (mServer.getServer().isCustomServer() ? "http://" + mServer.getIpAddress() + ":" + mServer.getPortAsString() : mServer.getServer().getApiAddress());
    }

    public String getDelegateName() {
        return mServer.getServerName();
    }

    public int getServerUID() {
        return mServer.getUId();
    }

    public void setCurrentStatus(int status) {
        currentStatus.set(status);
    }

}
