package com.vrlcrypt.arkmonitor.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.vrlcrypt.arkmonitor.adapters.callback.SettingServerDelegate;
import com.vrlcrypt.arkmonitor.models.Server;
import com.vrlcrypt.arkmonitor.models.Settings;

public class SettingViewModel extends AndroidViewModel {

    private Settings settings;

    private ArrayAdapter<String> serverList;

    private SettingServerDelegate settingServerDelegate;

    public SettingViewModel(@NonNull Application application, Settings settings, SettingServerDelegate settingServerDelegate) {
        super(application);

        this.settings = settings;
        this.settingServerDelegate = settingServerDelegate;

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

    public SettingServerDelegate getSettingServerDelegate() {
        return settingServerDelegate;
    }

    public void updateServerSettings(String username, String arkAddress, String publicKey, String ipAddress, int port, boolean sslEnabled, Server server, long notificationInterval) {
        settings.setUsername(username);
        settings.setArkAddress(arkAddress);
        settings.setPublicKey(publicKey);
        settings.setIpAddress(ipAddress);
        settings.setPort(port);
        settings.setSslEnabled(sslEnabled);
        settings.setServer(server);
        settings.setNotificationInterval(notificationInterval);
    }

    public Settings getSettings() {
        return settings;
    }


}
