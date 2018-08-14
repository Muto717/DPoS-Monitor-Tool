package com;

import android.util.Log;

import com.vrlcrypt.arkmonitor.models.Block;
import com.vrlcrypt.arkmonitor.models.BlockHeight;
import com.vrlcrypt.arkmonitor.models.Delegate;
import com.vrlcrypt.arkmonitor.models.NextForger;
import com.vrlcrypt.arkmonitor.models.Server;
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
        settings.setServerName("pieface");
        settings.setServer(Server.custom);
        settings.setIpAddress("80.211.102.41");
        settings.setPort(4100);
    }

    /*@Test
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
    public void testNextForger() {
        ArkService2.getInstance().getNextForgers().subscribe(
                nextForger -> System.out.println("Size: " + nextForger.getDelegates().size()),
                throwable -> System.out.println(throwable.getMessage())
        );
    }*/

    @Test
    public void testTogether() {
        Observable.zip(
                ArkService2.getInstance().getDelegate(settings), ArkService2.getInstance().getBlockHeight(settings), ArkService2.getInstance().getBlocks(settings, 100), ArkService2.getInstance().getNextForgers(settings),
                (delegate, blockHeight, blocks, nextForger) -> {
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
                    System.out.println("floor(" + blockHeight.getHeight() + "/" + "51) + (" +blockHeight.getHeight() + " % 51 > 0 ? 1 :0)" + " ------- " + (Math.floor(blockHeight.getHeight() / 51) + (blockHeight.getHeight() % 51 > 0 ? 1 : 0)));

                    status.setDelegateRound(Status.round(status.getLastBlock().getHeight(), 51));
                    System.out.println("floor(" +status.getLastBlock().getHeight()+ "/" + "51) + (" + status.getLastBlock().getHeight() + " % 51 > 0 ? 1 :0)" + " ------- " + (Math.floor(status.getLastBlock().getHeight() / 51) + (status.getLastBlock().getHeight() % 51 > 0 ? 1 : 0)));

                    status.setAwaitingSlot((int) (status.getNetworkRound() - status.getDelegateRound()));
                    System.out.println("NetworkRound: " + status.getNetworkRound() + " - DelegateRound: " + status.getDelegateRound());

                    if (status.getAwaitingSlot() == Status.FORGING) {
                        System.out.println("Forging");
                    } else if (!delegate.isRoundDelegate() && status.getAwaitingSlot() == Status.MISSING) {
                        System.out.println("Missing");
                    } else if (!delegate.isRoundDelegate() && status.getAwaitingSlot() > Status.MISSING) {
                        System.out.println("Not Forging");
                    } else if (status.getAwaitingSlot() == Status.MISSING) {
                        System.out.println("Awaiting Slot");
                    }

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
