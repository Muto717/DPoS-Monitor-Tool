package com.vrlcrypt.arkmonitor.persistance;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.vrlcrypt.arkmonitor.models.ServerSetting;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

@Database(entities = {ServerSetting.class}, version = 2)
@TypeConverters(com.vrlcrypt.arkmonitor.persistance.TypeConverters.class)
public abstract class SettingsDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "settings-database";

    private static volatile SettingsDatabase sInstance;

    public abstract SettingDao settingDao();

    public static SettingsDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (SettingsDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(
                            context,
                            SettingsDatabase.class,
                            DATABASE_NAME
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }

        return sInstance;
    }

    public Completable insert (final ServerSetting serverSetting) {
        return Completable.fromAction(() -> settingDao().insert(serverSetting)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable delete (final ServerSetting serverSetting) {
        return Completable.fromAction(() -> settingDao().delete(serverSetting)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Maybe<Integer> getCount () {
        return Maybe.fromCallable(() -> settingDao().getCount()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Maybe<ServerSetting> getServerSettingByName(String name) {
        return Maybe.fromCallable(() -> settingDao().getByName(name)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

}
