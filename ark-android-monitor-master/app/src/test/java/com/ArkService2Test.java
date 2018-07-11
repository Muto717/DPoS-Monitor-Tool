package com;

import android.util.Log;

import com.vrlcrypt.arkmonitor.models.BlockHeight;
import com.vrlcrypt.arkmonitor.models.ServerSetting;
import com.vrlcrypt.arkmonitor.services.ArkService2;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.functions.Consumer;

public class ArkService2Test {

    private ServerSetting settings;

    @Before
    public void before () {
        settings = new ServerSetting();
        settings.setIpAddress("https://node1.arknet.cloud/api/");
        settings.setPort(-1);
    }

    @Test
    public void testGetHeight() {
        ArkService2.getInstance().getBlockHeight(settings).subscribe(new Consumer<BlockHeight>() {
            @Override
            public void accept(BlockHeight blockHeight) throws Exception {
                System.out.println(blockHeight.getHeight());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                System.out.println(throwable.getMessage());
            }
        });
    }

}
