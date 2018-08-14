package com.vrlcrypt.arkmonitor.services;

import android.arch.persistence.room.Delete;
import android.util.Log;

import com.google.gson.Gson;
import com.vrlcrypt.arkmonitor.models.Block;
import com.vrlcrypt.arkmonitor.models.BlockHeight;
import com.vrlcrypt.arkmonitor.models.Delegate;
import com.vrlcrypt.arkmonitor.models.NextForger;
import com.vrlcrypt.arkmonitor.models.ServerSetting;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArkService2 {

    public static final String TAG = ArkService2.class.getSimpleName();

    private static ArkService2 sInstance = null;

    private static final String IP_ATTR = "ip";
    private static final String PORT_ATTR = "port";

    private static final String BLOCKS_URL = "blocks/";
    private static final String DELEGATE_URL = "delegates/get/";
    private static final String BLOCK_URL = "blocks";
    private static final String NEXT_FORGER_URL = "delegates/getNextForgers";


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
                .fromCallable(() -> client.newCall(createRequest((settings.getServer().isCustomServer() ? "http://" + settings.getIpAddress() + ":" + settings.getPortAsString() + "/" : "https://" + settings.getServer().getApiAddress()) + "/api/" + BLOCKS_URL, "getHeight", settings)).execute()) //Todo remove
                .map(response -> {
                    Log.d(TAG, "getBlockHeight: " + response);
                    int code = response.code();

                    if (code >= 400 && code <= 499) {                        //Client error
                        return new BlockHeight(false, -1, "");
                    } else if (code >= 500 && code <= 599) {                        //Server error
                        return new BlockHeight(false, -1, "");
                    } else {
                        return new Gson().fromJson(response.body().string(), BlockHeight.class);
                    }

                });
    }

    public Observable<List<Block>> getBlocks(final ServerSetting settings, int amount) {
        return Observable
                .fromCallable(() -> client.newCall(createRequest((settings.getServer().isCustomServer() ? "http://" + settings.getIpAddress() + ":" + settings.getPortAsString() + "/" : "https://" + settings.getServer().getApiAddress()) +  BLOCK_URL, "?orderBy=height:desc&limit=" + amount, null)).execute())
                .map(response -> {
                    Log.d(TAG, "getBlocks: " + response);

                    JSONArray array = new JSONObject(response.body().string()).getJSONArray("blocks");
                    List<Block> blocks = new ArrayList<>();

                    for (int i = 0; i < array.length(); i++) {
                        blocks.add(Block.fromJson(array.getJSONObject(i)));
                    }

                    return blocks;
                });
    }

    public Observable<Delegate> getDelegate(final ServerSetting settings) {
        return Observable
                .fromCallable(() -> client.newCall(createRequest((settings.getServer().isCustomServer() ? "http://" + settings.getIpAddress() + ":" + settings.getPortAsString() + "/" : "https://" + settings.getServer().getApiAddress()) +  DELEGATE_URL, "?username=" + settings.getServerName(), null)).execute())
                .map(response -> {
                    Log.d(TAG, "getDelegate: " + response);
                    return Delegate.fromJson(new JSONObject(response.body().string()).getJSONObject("delegate"));
                });
    }

    public Observable<NextForger> getNextForgers(final ServerSetting settings) {
        return Observable
                .fromCallable(() -> client.newCall(createRequest((settings.getServer().isCustomServer() ? "http://" + settings.getIpAddress() + ":" + settings.getPortAsString() + "/" : "https://" + settings.getServer().getApiAddress()) +  NEXT_FORGER_URL, "?limit=51", null)).execute())
                .map(response -> {
                    Log.d(TAG, "getNextForgers: " + response);
                    return NextForger.fromJson(response.body().string());
                });
    }

    private Request createRequest(String url, String endPoint, ServerSetting settings) {
        String urlRequest = url + endPoint;
        return new Request.Builder()
                .url(urlRequest)
                .build();
    }

}
