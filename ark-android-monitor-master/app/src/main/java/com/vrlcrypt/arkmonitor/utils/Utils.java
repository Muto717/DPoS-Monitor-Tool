package com.vrlcrypt.arkmonitor.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.vrlcrypt.arkmonitor.models.Server;
import com.vrlcrypt.arkmonitor.models.ServerSetting;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Utils {
    private static final Pattern PATTERN_IP_ADDRESS = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final String ALARM_ATTR = "alarm_key";
    private static final int MAX_PORT_NUMBER = 65535;
    private static final String START_DATE = "21/03/2017 13:00:00";
    private static final String FORMAT_DATE = "dd/MM/yyyy HH:mm:ss";
    private static final String TIME_ZONE = "UTC";

    private Utils() {
    }

    public static boolean validateArkAddress(final String arkAddress) {
        return arkAddress != null && arkAddress.length() > 0;
    }

    public static boolean validateIpAddress(final String ip) {
        return ip != null && PATTERN_IP_ADDRESS.matcher(ip).matches();
    }

    public static boolean validatePort(final int port) {
        return port > 0 && port <= MAX_PORT_NUMBER;
    }

    public static boolean validatePort(final String port) {
        return isInteger(port) && validatePort(Integer.valueOf(port));
    }

    private static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    private static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static boolean isOnline(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean saveSettings(Activity activity, ServerSetting serverSetting) {
        if (serverSetting.getServer().isCustomServer()) {
            if (!validateIpAddress(serverSetting.getIpAddress())) {
                return false;
            }

            if (!validatePort(serverSetting.getPort())) {
                return false;
            }
        }

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(activity);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        prefsEditor.putString(ServerSetting.USERNAME_ATTR, serverSetting.getUsername());
        prefsEditor.putString(ServerSetting.ARK_ADDRESS_ATTR, serverSetting.getArkAddress());
        prefsEditor.putString(ServerSetting.PUBLIC_KEY_ATTR, serverSetting.getPublicKey());
        prefsEditor.putString(ServerSetting.IP_ATTR, serverSetting.getIpAddress());
        prefsEditor.putInt(ServerSetting.PORT_ATTR, serverSetting.getPort());
        prefsEditor.putBoolean(ServerSetting.SSL_ENABLED_ATTR, serverSetting.getSslEnabled());
        prefsEditor.putInt(ServerSetting.SERVER_ATTR, serverSetting.getServer().getId());
        prefsEditor.putLong(ServerSetting.NOTIFICATION_INTERVAL_ATTR, serverSetting.getNotificationInterval());

        return prefsEditor.commit();
    }

    public static ServerSetting getSettings(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        ServerSetting serverSetting = new ServerSetting();
        serverSetting.setUsername(mPrefs.getString(ServerSetting.USERNAME_ATTR, null));
        serverSetting.setArkAddress(mPrefs.getString(ServerSetting.ARK_ADDRESS_ATTR, null));
        serverSetting.setPublicKey(mPrefs.getString(ServerSetting.PUBLIC_KEY_ATTR, null));
        serverSetting.setIpAddress(mPrefs.getString(ServerSetting.IP_ATTR, null));
        serverSetting.setPort(mPrefs.getInt(ServerSetting.PORT_ATTR, -1));
        serverSetting.setSslEnabled(mPrefs.getBoolean(ServerSetting.SSL_ENABLED_ATTR, false));
        serverSetting.setServerById(mPrefs.getInt(ServerSetting.SERVER_ATTR, Server.ark1.getId()));
        serverSetting.setNotificationInterval(mPrefs.getLong(ServerSetting.NOTIFICATION_INTERVAL_ATTR, AlarmManager.INTERVAL_FIFTEEN_MINUTES));

        return serverSetting;
    }

    public static void showMessage(String message, View view) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static String formatDecimal(double value) {
        try {
            double total = convertToArkBase(value);
            DecimalFormat df = new DecimalFormat("#0.00000000");
            return df.format(total);
        } catch (NumberFormatException ex) {
            return "";
        }
    }

    public static double convertToArkBase(double value){
        return value * Math.pow(10, -8);
    }

    public static boolean validatePublicKey(String publicKey) {
        return publicKey != null && publicKey.length() > 0;
    }

    public static boolean validateUsername(String username) {
        return username != null && username.length() > 0 && username.length() <= 20;
    }


    public static CharSequence getTimeAgo(long timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE, Locale.ENGLISH);
        sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));

        Date d;
        try {
            d = sdf.parse(START_DATE);
        } catch (ParseException e) {
            return "";
        }

        long t = d.getTime() / 1000;

        Date timeStart = new Date((timestamp + t) * 1000);

        long now = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE)).getTimeInMillis();

        return DateUtils.getRelativeTimeSpanString(timeStart.getTime(), now, DateUtils.SECOND_IN_MILLIS);
    }

    public static long getTimeInMillisUntilNow(long timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE, Locale.ENGLISH);
        sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, TIME_ZONE));

        Date d;
        try {
            d = sdf.parse(START_DATE);
        } catch (ParseException e) {
            return -1;
        }

        long t = d.getTime() / 1000;

        Date timeStart = new Date((timestamp + t) * 1000);

        long now = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

        return now - timeStart.getTime();
    }

    public static boolean alarmEnabled(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return mPrefs.getBoolean(ALARM_ATTR, false);
    }

    public static boolean enableAlarm(Context context, boolean enable){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putBoolean(ALARM_ATTR, enable);
        return prefsEditor.commit();
    }
}