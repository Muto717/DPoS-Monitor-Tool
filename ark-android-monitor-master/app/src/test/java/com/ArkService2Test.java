package com;

import android.util.Log;

import com.vrlcrypt.arkmonitor.models.BlockHeight;
import com.vrlcrypt.arkmonitor.models.Delegate;
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

}
