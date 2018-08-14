package com.vrlcrypt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Pair;

import com.vrlcrypt.arkmonitor.MainActivity;
import com.vrlcrypt.arkmonitor.R;
import com.vrlcrypt.arkmonitor.models.Status;

import java.util.List;


public class StatusServiceNotification {

    public static final int NOTIFICATION_ID = 1992;
    private static final String NOTIFICATION_CHANNEL = "NETWORK_CHANNEL_02";


    public static Notification getNotification(Context context, List<Pair<String, Integer>> delegateStatus) {
        NotificationManager mNotificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder
                = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL);

        if (mNotificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL) == null)
                mNotificationManager.createNotificationChannel(
                        new NotificationChannel(NOTIFICATION_CHANNEL, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH)
                );

            mNotificationManager.cancel(NOTIFICATION_ID);

            Intent resultIntent = new Intent(context, MainActivity.class);

            resultIntent.setAction(Intent.ACTION_MAIN);
            resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent resultPendingIntent
                    = PendingIntent.getActivity(context, 0, resultIntent, 0);

            int countAwaiting = 0, countForging = 0, countOther = 0;

            for (Pair<String, Integer> pair : delegateStatus) {
                if (pair.second == Status.FORGING) {
                    countForging++;
                } else if (pair.second == Status.AWAITING_SLOT || pair.second == Status.AWAITING_STATUS) {
                    countAwaiting++;
                } else {
                    countOther++;
                }
            }

            mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText("Total Delegates: " + delegateStatus.size() + " Forging(" + countForging + ")" + " Awaiting Slot(" + countAwaiting + ")" + " Other/Missing(" + countOther + ")")
                    .setSound(null)
                    .setContentIntent(resultPendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                mBuilder.setChannelId(NOTIFICATION_CHANNEL);

            return mBuilder.build();
        }

        return mBuilder.build();
    }

}
