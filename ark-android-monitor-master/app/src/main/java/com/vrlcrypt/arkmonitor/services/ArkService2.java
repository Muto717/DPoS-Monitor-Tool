package com.vrlcrypt.arkmonitor.services;

import android.arch.persistence.room.Delete;

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
                .fromCallable(() -> client.newCall(createRequest("https://node1.arknet.cloud/api/" + BLOCKS_URL, "getHeight", settings)).execute()) //Todo remove
                .map(response -> {
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

    public Observable<List<Block>> getBlocks(int amount) {
        return Observable
                .fromCallable(() -> client.newCall(createRequest("https://node1.arknet.cloud/api/" + BLOCK_URL, "?orderBy=height:desc&limit=" + amount, null)).execute())
                .map(response -> {
                    JSONArray array = new JSONObject(response.body().string()).getJSONArray("blocks");
                    List<Block> blocks = new ArrayList<>();

                    for (int i = 0; i < array.length(); i++) {
                        blocks.add(Block.fromJson(array.getJSONObject(i)));
                    }

                    return blocks;
                });
    }

    public Observable<Delegate> getDelegate(String username) {
        return Observable
                .fromCallable(() -> client.newCall(createRequest("https://node1.arknet.cloud/api/" + DELEGATE_URL, "?username=" + username, null)).execute())
                .map(response -> Delegate.fromJson(new JSONObject(response.body().string()).getJSONObject("delegate")));
    }

    public Observable<NextForger> getNextForgers() {
        return Observable
                .fromCallable(() -> client.newCall(createRequest("https://node1.arknet.cloud/api/" + NEXT_FORGER_URL, "?limit=51", null)).execute())
                .map(response -> NextForger.fromJson(response.body().string()));
    }

    private Request createRequest(String url, String endPoint, ServerSetting settings) {
        String urlRequest = url + endPoint;

        //urlRequest = replaceURLWithSettings(urlRequest, settings);

        return new Request.Builder()
                .url(urlRequest)
                .build();
    }

}
