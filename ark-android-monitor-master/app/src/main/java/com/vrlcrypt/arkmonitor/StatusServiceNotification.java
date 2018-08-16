package com.vrlcrypt.arkmonitor;

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
import com.vrlcrypt.arkmonitor.models.ServerSetting;
import com.vrlcrypt.arkmonitor.models.Status;

import java.util.List;


public class StatusServiceNotification {

    public static final int NOTIFICATION_ID = 100001;
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

            mBuilder.setSmallIcon(R.drawable.ark_red)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText("Updating Delegate Status every 10secs")
                    .setSound(null)
                    .setContentIntent(resultPendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                mBuilder.setChannelId(NOTIFICATION_CHANNEL);

            return mBuilder.build();
        }

        return mBuilder.build();
    }

    public static Notification statusUpdate(Context context, ServerSetting serverSetting, Pair<Integer, Integer> pair) {
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

            String content = "";

            if (pair.second.equals(Status.NOT_FORGING))
                content = "Server: " + (serverSetting.getServer().isCustomServer() ? serverSetting.getIpAddress() : serverSetting.getServer().getName()) + " has stopped forging!";
            else if (pair.second.equals(Status.MISSED_AWAITING_SLOT) || pair.second.equals(Status.MISSING))
                content = "Server: " + (serverSetting.getServer().isCustomServer() ? serverSetting.getIpAddress() : serverSetting.getServer().getName()) + " missed last block!";
            //else if (pair.second.equals(Status.FORGING))
             //   content = "Server: " + (serverSetting.getServer().isCustomServer() ? serverSetting.getIpAddress() : serverSetting.getServer().getName()) + " is currently forging!";

            mBuilder.setSmallIcon(R.drawable.ark_red)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(content)
                    .setContentIntent(resultPendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                mBuilder.setChannelId(NOTIFICATION_CHANNEL);

            return mBuilder.build();
        }

        return mBuilder.build();
    }

}
