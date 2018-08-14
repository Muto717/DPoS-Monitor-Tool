package com.vrlcrypt.arkmonitor.adapters.viewModel;

import android.arch.lifecycle.ViewModel;

import com.vrlcrypt.arkmonitor.models.ServerSetting;

public class ServerViewModel extends ViewModel {

    private ServerSetting mServer;

    public ServerViewModel(ServerSetting mServer) {
        this.mServer = mServer;
    }

    public String getAddress( ) {
        return (mServer.getServer().isCustomServer() ? "http://" + mServer.getIpAddress() + ":" + mServer.getPortAsString() : mServer.getServer().getApiAddress());
    }

    public String getDelegateName() {
        return mServer.getServerName();
    }

}
