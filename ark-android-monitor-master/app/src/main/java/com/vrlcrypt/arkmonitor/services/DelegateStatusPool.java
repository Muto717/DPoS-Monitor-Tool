package com.vrlcrypt.arkmonitor.services;

import android.util.Log;
import android.util.Pair;

import com.vrlcrypt.arkmonitor.models.Block;
import com.vrlcrypt.arkmonitor.models.BlockHeight;
import com.vrlcrypt.arkmonitor.models.Delegate;
import com.vrlcrypt.arkmonitor.models.NextForger;
import com.vrlcrypt.arkmonitor.models.ServerSetting;
import com.vrlcrypt.arkmonitor.models.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function4;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class DelegateStatusPool implements Consumer<Long> {

    public static final String TAG = DelegateStatusPool.class.getSimpleName();

    public static final long UPDATE_TIME = 10000;

    private static DelegateStatusPool sInstance;

    private List<ServerSetting> mDelegates;

    private CompositeDisposable mDisposables;

    public PublishSubject<List<Pair<String, Integer>>> mStatusPublisher = PublishSubject.create();

    private List<Pair<String, Integer>> mAwaitingPublish = new ArrayList<>();

    public DelegateStatusPool() {
        this.mDelegates = new ArrayList<>();
        this.mDisposables = new CompositeDisposable();

        mDisposables.add(Observable.interval(UPDATE_TIME, TimeUnit.MILLISECONDS).subscribe(this));
    }

    public static DelegateStatusPool getInstance() {
        if (sInstance == null)
            sInstance = new DelegateStatusPool();

        return sInstance;
    }

    public void insertDelegate(ServerSetting delegate) {
        mDelegates.add(delegate);
    }

    @Override
    public void accept(Long o) {
        Log.d(TAG, "----------- TICK -----------");

        if (!mDelegates.isEmpty()) {
            ArkService2 service = ArkService2.getInstance();

            for (ServerSetting serverSetting : mDelegates) {
                mDisposables.add(Observable.zip(service.getDelegate(serverSetting), service.getBlocks(serverSetting,100), service.getNextForgers(serverSetting), service.getBlockHeight(serverSetting),
                        (delegate, blocks, nextForger, blockHeight) -> {
                            for (Block block : blocks) {
                                if (block.getGeneratorPublicKey().equals(delegate.getPublicKey())) {
                                    delegate.setLastBlock(block);
                                    delegate.setBlocksAt(block.getTimestamp());
                                    break;
                                }
                            }

                            for (String publicKey : nextForger.getDelegates()) {
                                if (delegate.getPublicKey().equals(publicKey)) {
                                    delegate.setForgingTime(nextForger.getDelegates().indexOf(publicKey) * 8);
                                    delegate.setRoundDelegate(true);
                                }
                            }

                            Status status = new Status();
                            status.setLastBlock(delegate.getLastBlock());
                            status.setBlockAt(Status.epochStamp(status.getLastBlock().getTimestamp()));
                            status.setNetworkRound(Status.round(blockHeight.getHeight(), 51));
                            status.setDelegateRound(Status.round(status.getLastBlock().getHeight(), 51));
                            status.setAwaitingSlot((int) (status.getNetworkRound() - status.getDelegateRound()));

                            delegate.setStatus(status);

                            return delegate;
                        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                delegate -> {
                                    int status = getStatus(delegate);
                                    mAwaitingPublish.add(new Pair<>(serverSetting.getServerName(), status));

                                    if (mAwaitingPublish.size() == mDelegates.size()) { //Publish and clear
                                        mStatusPublisher.onNext(mAwaitingPublish);
                                        mAwaitingPublish.clear();
                                    }
                                },
                                throwable -> {
                                    int status = -1;
                                    mAwaitingPublish.add(new Pair<>(serverSetting.getServerName(), status));

                                    if (mAwaitingPublish.size() == mDelegates.size()) { //Publish and clear
                                        mStatusPublisher.onNext(mAwaitingPublish);
                                        mAwaitingPublish.clear();
                                    }
                                }
                        ));
            }

        }
    }

    public int getStatus(Delegate delegate) {
        if (delegate.getStatus().getAwaitingSlot() == Status.FORGING) {
            Log.d(TAG, delegate.getUsername() + " : Forging");
            return Status.FORGING;
        } else if (!delegate.isRoundDelegate() && delegate.getStatus().getAwaitingSlot() == Status.MISSING) {
            Log.d(TAG, delegate.getUsername() + " : Missing");
            return Status.MISSING;
        } else if (!delegate.isRoundDelegate() && delegate.getStatus().getAwaitingSlot() > Status.MISSING) {
            return Status.NOT_FORGING;
        } else if (delegate.getStatus().getAwaitingSlot() == Status.MISSING) {
            Log.d(TAG, delegate.getUsername() + " : Awaiting Slot");
            return Status.AWAITING_SLOT;
        } else if (delegate.getStatus().getAwaitingSlot() == Status.NOT_FORGING) {
            Log.d(TAG, delegate.getUsername() + " : Missed Awaiting Slot");
            return Status.MISSED_AWAITING_SLOT;
        } else {
            Log.d(TAG, delegate.getUsername() + " : Not Forging");
            return Status.NOT_FORGING;
        }
    }

    public boolean containsDelegate (String delegateName) {
        return mDelegates.contains(delegateName);
    }

    public int getDelegateCount () {
        return mDelegates.size();
    }

}