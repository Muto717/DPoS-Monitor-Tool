package com.vrlcrypt.arkmonitor.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public abstract class BindableService extends Service {
	private final IBinder binder = new LocalBinder();
    static private final String TAG = BindableService.class.getSimpleName();

    public class LocalBinder extends Binder {
        public BindableService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BindableService.this;
        }
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(TAG, "onBind()");
		return binder;
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

}
