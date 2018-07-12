package com.vrlcrypt.arkmonitor.models;

public class BlockHeight {

    private boolean success;

    private long height;

    private String id;

    public BlockHeight(boolean success, long height, String id) {
        this.success = success;
        this.height = height;
        this.id = id;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getHeight() {
        return height;
    }

    public String getId() {
        return id;
    }

}
