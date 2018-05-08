package com.vrlcrypt.arkmonitor.persistance;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.vrlcrypt.arkmonitor.models.ServerSetting;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface SettingDao {

    @Delete void delete(ServerSetting serverSetting);

    @Insert(onConflict = OnConflictStrategy.REPLACE) void insert(ServerSetting serverSetting);

    @Query("SELECT * FROM SERVER_SETTINGS")
    Flowable<List<ServerSetting>> getSettings();

    @Query("SELECT COUNT(*)  FROM SERVER_SETTINGS")
    int getCount();

    @Query("SELECT * FROM SERVER_SETTINGS WHERE serverName = :name")
    ServerSetting getByName(String name);

}
