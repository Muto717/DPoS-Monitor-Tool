package com.vrlcrypt.arkmonitor.models;

import com.google.gson.Gson;

import java.util.List;

public class NextForger {

    private boolean success;

    private long currentBlock;

    private long currentSlot;

    private List<String> delegates;

    public NextForger (boolean success, long currentBlock, long currentSlot, List<String> delegates) {
        this.success = success;
        this.currentBlock = currentBlock;
        this.currentSlot = currentSlot;
        this.delegates = delegates;
    }

    public static NextForger fromJson(String json) {
        return new Gson().fromJson(json, NextForger.class);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getCurrentBlock() {
        return currentBlock;
    }

    public void setCurrentBlock(long currentBlock) {
        this.currentBlock = currentBlock;
    }

    public long getCurrentSlot() {
        return currentSlot;
    }

    public void setCurrentSlot(long currentSlot) {
        this.currentSlot = currentSlot;
    }

    public List<String> getDelegates() {
        return delegates;
    }

    public void setDelegates(List<String> delegates) {
        this.delegates = delegates;
    }

}
