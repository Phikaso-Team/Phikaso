package com.phikaso.app.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.phikaso.app.util.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MyNotificationService extends NotificationListenerService {
    private static final String TAG = "NotificationService";

    public static final String ACTION_NOTIFICATION_BROADCAST = "phikaso_notification_service";
    public static final String EXTRA_TIME = "extra_time";
    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_TEXT = "extra_text";
    public static final String EXTRA_ROOM = "extra_room";

    public static final String TARGET_APP_PACKAGE = "com.kakao.talk"; // 알림을 읽어올 앱

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int r = super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "NotificationService 서비스 시작됨");
        return r;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        final String packageName = sbn.getPackageName();
        if (!Objects.equals(packageName, TARGET_APP_PACKAGE)) return;

        final Bundle extras = sbn.getNotification().extras;
        String       name   = extras.getString(Notification.EXTRA_TITLE);
        CharSequence text   = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence room   = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
        if (name == null || text == null) return;
        if (room == null) room = "";

        @SuppressLint("SimpleDateFormat")
        final SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a");
        final Intent intent = new Intent(ACTION_NOTIFICATION_BROADCAST);
        intent.putExtra(EXTRA_TIME, fullDateFormat.format(new Date()).toString());
        intent.putExtra(EXTRA_NAME, name);
        intent.putExtra(EXTRA_TEXT, text.toString());
        intent.putExtra(EXTRA_ROOM, room.toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        PreferenceManager.setString(this, "kakao-name", name);
    }
}
