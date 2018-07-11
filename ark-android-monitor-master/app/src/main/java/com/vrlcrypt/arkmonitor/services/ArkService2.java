package com.vrlcrypt.arkmonitor.services;

import com.google.gson.Gson;
import com.vrlcrypt.arkmonitor.models.BlockHeight;
import com.vrlcrypt.arkmonitor.models.ServerSetting;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArkService2 {

    private static ArkService2 sInstance = null;

    private static final String IP_ATTR = "ip";
    private static final String PORT_ATTR = "port";
    private static final String CUSTOM_API_URL = IP_ATTR + ":" + PORT_ATTR + "/api/";

    private static final String BLOCKS_URL = CUSTOM_API_URL + "blocks";

    private static final String HTTP_PROTOCOL = "http://";
    private static final String HTTPS_PROTOCOL = "https://";

    private final OkHttpClient client;

    public static ArkService2 getInstance() {
        if (sInstance == null)
            sInstance = new ArkService2();

        return sInstance;
    }

    private ArkService2() {
        client = new OkHttpClient();
    }

    public Observable<BlockHeight> getBlockHeight(final ServerSetting settings) {
        return Observable
                .fromCallable(() -> client.newCall(createRequest(BLOCKS_URL, "getHeight", settings)).execute())
                .map(response -> {
                    int code = response.code();

                    if (code >= 400 && code <= 499) {                        //Client error
                        return new BlockHeight(false, -1, -1);
                    } else if (code >= 500 && code <= 599) {                        //Server error
                        return new BlockHeight(false, -1, -1);
                    } else {
                        return new Gson().fromJson(response.body().string(), BlockHeight.class);
                    }

                });
    }

    private Request createRequest(String url, String endPoint, ServerSetting settings) {
        String urlRequest = url + endPoint;

        urlRequest = replaceURLWithSettings(urlRequest, settings);

        return new Request.Builder()
                .url(urlRequest)
                .build();
    }

    private static String replaceURLWithSettings(String url, ServerSetting settings) {
        if (settings.getServer() != null && !settings.getServer().isCustomServer()) {
            String apiUrl = url.replace(CUSTOM_API_URL, "");
            return settings.getServer().getApiAddress() + apiUrl;
        }

        String apiUrl = url.replace(IP_ATTR, settings.getIpAddress());
        apiUrl = apiUrl.replace(PORT_ATTR, String.valueOf(settings.getPort()));
        return (settings.getSslEnabled() ? HTTPS_PROTOCOL : HTTP_PROTOCOL) + apiUrl;
    }

}
