package com.vrlcrypt.arkmonitor.adapters.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.vrlcrypt.arkmonitor.models.Server;
import com.vrlcrypt.arkmonitor.models.ServerSetting;
import com.vrlcrypt.arkmonitor.persistance.SettingsDatabase;

public class SettingViewModel extends AndroidViewModel {

    private ServerSetting serverSetting;

    private ArrayAdapter<String> serverList;

    public ObservableBoolean isCustomServer;

    public SettingViewModel(@NonNull Application application, ServerSetting serverSetting) {
        super(application);

        this.serverSetting = serverSetting;
        this.isCustomServer = new ObservableBoolean();

        setupServerList();
    }

    private void setupServerList() {
        this.serverList = new ArrayAdapter<>(getApplication(),
                android.R.layout.simple_spinner_item, Server.getServers());

        serverList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public ArrayAdapter<String> getServerList() {
        return serverList;
    }

    public void updateServerSettings(String username, String arkAddress, String publicKey, String ipAddress, int port, boolean sslEnabled, Server server, long notificationInterval) {
        this.serverSetting.setUsername(username);
        this.serverSetting.setArkAddress(arkAddress);
        this.serverSetting.setPublicKey(publicKey);
        this.serverSetting.setIpAddress(ipAddress);
        this.serverSetting.setPort(port);
        this.serverSetting.setSslEnabled(sslEnabled);
        this.serverSetting.setServer(server);
        this.serverSetting.setNotificationInterval(notificationInterval);

        SettingsDatabase.getInstance(getApplication()).insert(serverSetting).subscribe();
    }

    public void deleteServerSetting() {
        SettingsDatabase.getInstance(getApplication()).delete(serverSetting).subscribe();
    }

    public ServerSetting getServerSetting() {
        return this.serverSetting;
    }

}
