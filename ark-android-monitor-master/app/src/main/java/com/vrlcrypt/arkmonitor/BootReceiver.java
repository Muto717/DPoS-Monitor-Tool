package com.vrlcrypt.arkmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_BOOT_COMPLETED) ||
                    action.equals(Intent.ACTION_USER_PRESENT)) {

                Intent activityIntent = new Intent(context, StatusService.class); //Start the activity as user might have removed permissions or turned something off
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(activityIntent);
            }
        }
    }

}
