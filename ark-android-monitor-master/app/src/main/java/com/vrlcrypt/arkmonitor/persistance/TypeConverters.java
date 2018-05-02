package com.vrlcrypt.arkmonitor.persistance;

import android.arch.persistence.room.TypeConverter;

import com.vrlcrypt.arkmonitor.models.Server;

public class TypeConverters {

    @TypeConverter
    public static int getServerId(Server server) {
        return server == null ? Server.ark1.getId() : server.getId();
    }

    @TypeConverter
    public static Server getServerFromId(int id) {
        return Server.fromId(id);
    }

}
