package com.vrlcrypt.arkmonitor.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.logging.Logger;

public class Status {

    public static final int FORGING = 0,
    MISSING = 1,
    NOT_FORGING = 2,
    AWAITING_SLOT = 3,
    MISSED_AWAITING_SLOT = 4,
    AWAITING_STATUS = 5;

    private Long blocks;
    private Long height;

    private Block lastBlock;
    private long blockAt;

    private double networkRound;
    private double delegateRound;
    private int awaitingSlot;

    private static final String TAG = Status.class.getSimpleName();

    public Long getBlocks() {
        return blocks;
    }

    public void setBlocks(Long blocks) {
        this.blocks = blocks;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public static Status fromJson(JSONObject jsonObject) {
        Status status = new Status();

        if (jsonObject == null) {
            return status;
        }

        try {
            status.blocks = jsonObject.getLong("blocks");
        } catch (JSONException e) {
            Logger.getLogger(TAG).warning(String.format("status.blocks (%s)", e.getLocalizedMessage()));
        }

        try {
            status.height = jsonObject.getLong("height");
        } catch (JSONException e) {
            Logger.getLogger(TAG).warning(String.format("status.height (%s)", e.getLocalizedMessage()));
        }

        return status;
    }

    public Block getLastBlock() {
        return lastBlock;
    }

    public long getBlockAt() {
        return blockAt;
    }

    public double getNetworkRound() {
        return networkRound;
    }

    public double getDelegateRound() {
        return delegateRound;
    }

    public int getAwaitingSlot() {
        return awaitingSlot;
    }

    public void setLastBlock(Block lastBlock) {
        this.lastBlock = lastBlock;
    }

    public void setBlockAt(long blockAt) {
        this.blockAt = blockAt;
    }

    public void setNetworkRound(double networkRound) {
        this.networkRound = networkRound;
    }

    public void setDelegateRound(double delegateRound) {
        this.delegateRound = delegateRound;
    }

    public void setAwaitingSlot(int awaitingSlot) {
        this.awaitingSlot = awaitingSlot;
    }

    public static double round(long height, int activeDelegates) {
        if (height <= 0) return 0;
        return Math.floor(height / activeDelegates) + (height % activeDelegates > 0 ? 1 : 0);
    }

    public static long epochStamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 2, 21, 13, 0, 0);
        calendar.setTimeInMillis(calendar.getTimeInMillis() + timestamp);
        return calendar.getTimeInMillis();
    }

}
