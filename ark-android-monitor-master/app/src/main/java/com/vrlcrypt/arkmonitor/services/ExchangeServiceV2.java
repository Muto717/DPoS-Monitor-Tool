package com.vrlcrypt.arkmonitor.services;

import android.util.Log;

import com.vrlcrypt.arkmonitor.models.Ticker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExchangeServiceV2 {

    private static final String URL_TICKER = "https://bittrex.com/api/v1.1/public/getmarketsummary?market=btc-ark";
    private static final String BITCOIN_EUR_URL_TICKER = "https://www.bitstamp.net/api/v2/ticker/btceur/";
    private static final String BITCOIN_USD_URL_TICKER = "https://www.bitstamp.net/api/v2/ticker/btcusd/";

    private final OkHttpClient client;

    public ExchangeServiceV2() {
        client = new OkHttpClient();
    }

    public Observable<PriceTickers> btcPriceTickers() {
        return Observable.fromCallable(() -> {
            Log.d(ExchangeServiceV2.class.getSimpleName(), "Getting price tickers");

            Ticker btcUsd = null, btcEur = null, btc = null;

            try {
                btcUsd = parseResponse(client.newCall(getBtcUsdRequest()).execute(), false);
            } catch (InterruptedIOException ex) {
                Log.e(ExchangeServiceV2.class.getSimpleName(), "BTCUSD Price Ticker", ex.getCause());
            }

            try {
                btcEur = parseResponse(client.newCall(getBtcEurRequest()).execute(), false);
            } catch (InterruptedIOException ex) {
                Log.e(ExchangeServiceV2.class.getSimpleName(), "BTCEUR Price Ticker", ex.getCause());
            }

            try {
                btc = parseResponse(client.newCall(getBtcRequest()).execute(), true);
            } catch (InterruptedIOException ex) {
                Log.e(ExchangeServiceV2.class.getSimpleName(), "BTC Price Ticker", ex.getCause());
            }

            return new PriceTickers(btc, btcUsd, btcEur);
        }).repeatWhen(objectObservable -> Observable.timer(10, TimeUnit.SECONDS))
                .repeat(Long.MAX_VALUE)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private Ticker parseResponse(Response response, boolean isBtcArk) {
        if (response.isSuccessful()) {
            try {
                JSONObject jsonObject = null;
                String jsonData = response.body().string();

                if (!isBtcArk) {
                    Log.d(ExchangeServiceV2.class.getSimpleName(), jsonData);
                    jsonObject = new JSONObject(jsonData);
                } else {
                    JSONObject temp = new JSONObject(jsonData);

                    JSONArray tickers = temp.getJSONArray("result");

                    if (null != tickers && tickers.length() > 0)
                        jsonObject = tickers.getJSONObject(0);
                }

                return Ticker.fromJson(jsonObject);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private Request getBtcUsdRequest() {
        return new Request.Builder()
                .url(BITCOIN_USD_URL_TICKER)
                .build();
    }

    private Request getBtcEurRequest() {
        return new Request.Builder()
                .url(BITCOIN_EUR_URL_TICKER)
                .build();
    }

    private Request getBtcRequest() {
        return new Request.Builder()
                .url(URL_TICKER)
                .build();
    }

    public class PriceTickers {

        private Ticker btc;

        private Ticker btcUsd;

        private Ticker btcEur;

        public PriceTickers(Ticker btc, Ticker btcUsd, Ticker btcEur) {
            this.btc = btc;
            this.btcEur = btcEur;
            this.btcUsd = btcUsd;
        }

        public Ticker getBtcUsd() {
            return btcUsd;
        }

        public Ticker getBtcEur() {
            return btcEur;
        }

        public Ticker getBtc() {
            return btc;
        }
    }

}
