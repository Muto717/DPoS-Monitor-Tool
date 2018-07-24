package com;

import android.util.Log;

import com.vrlcrypt.arkmonitor.models.Block;
import com.vrlcrypt.arkmonitor.models.BlockHeight;
import com.vrlcrypt.arkmonitor.models.Delegate;
import com.vrlcrypt.arkmonitor.models.ServerSetting;
import com.vrlcrypt.arkmonitor.models.Status;
import com.vrlcrypt.arkmonitor.services.ArkService;
import com.vrlcrypt.arkmonitor.services.ArkService2;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;

public class ArkService2Test {

    private ServerSetting settings;

    @Before
    public void before() {
        settings = new ServerSetting();
        settings.setIpAddress("https://node1.arknet.cloud/api/");
        settings.setPort(-1);
    }

    @Test
    public void testGetHeight() {
        ArkService2.getInstance().getBlockHeight(settings).subscribe(
                blockHeight -> System.out.println(blockHeight.getHeight()),
                throwable -> System.out.println(throwable.getMessage())
        );
    }

    @Test
    public void testGetDelegate() {
        ArkService2.getInstance().getDelegate("pieface")
                .subscribe(
                        delegate -> System.out.println(delegate.getAddress()),
                        throwable -> System.out.println(throwable.getMessage())
                );
    }

    @Test
    public void testGetBlocks() {
        ArkService2.getInstance().getBlocks(100)
                .subscribe(
                        blocks -> System.out.println(blocks.size()),
                        throwable -> System.out.println(throwable.getMessage())
                );
    }

    @Test
    public void testTogether() {
        Observable.zip(
                ArkService2.getInstance().getDelegate("pieface"), ArkService2.getInstance().getBlockHeight(settings), ArkService2.getInstance().getBlocks(100),
                (delegate, blockHeight, blocks) -> {
                    for (Block block : blocks) {
                        if (block.getGeneratorPublicKey().equals(delegate.getPublicKey())) {
                            delegate.setLastBlock(block);
                            delegate.setBlocksAt(block.getTimestamp());
                            break;
                        }
                    }

                    Status status = new Status();
                    status.setLastBlock(delegate.getLastBlock());
                    status.setBlockAt(Status.epochStamp(status.getLastBlock().getTimestamp()));
                    status.setNetworkRound(Status.round(blockHeight.getHeight(), 51));
                    status.setDelegateRound(Status.round(status.getLastBlock().getHeight(), 51));
                    status.setAwaitingSlot((int) (status.getNetworkRound() - status.getDelegateRound()));

                    return status;
                }
        ).subscribe(new Consumer<Status>() {
            @Override
            public void accept(Status status) throws Exception {
                System.out.println("Awaiting Slot: " + status.getAwaitingSlot());
            }
        }, throwable -> System.out.println(throwable.getMessage()));
    }

}
