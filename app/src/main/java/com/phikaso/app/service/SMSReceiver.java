package com.phikaso.app.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSReceiver";
    public  static final String ACTION_NOTIFICATION_BROADCAST = "phikaso_sms_receiver";

    public static final String EXTRA_TIME = "extra_time";
    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_TEXT = "extra_text";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = parseSmsMessage(bundle);
        if (messages.length > 0) {
            String sender = messages[0].getOriginatingAddress();
            Date receivedDate = new Date(messages[0].getTimestampMillis());
            String contents = messages[0].getMessageBody();

            Log.d(TAG, "onReceive: sender:" + sender);
            Log.d(TAG, "onReceive: receivedDate: " + receivedDate);
            Log.d(TAG, "onReceive: contents: " + contents);

            @SuppressLint("SimpleDateFormat")
            final SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a");
            final Intent intent2 = new Intent(ACTION_NOTIFICATION_BROADCAST);
            intent2.putExtra(EXTRA_TIME, fullDateFormat.format(receivedDate).toString());
            intent2.putExtra(EXTRA_NAME, sender);
            intent2.putExtra(EXTRA_TEXT, contents);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
        }
    }

    private SmsMessage[] parseSmsMessage(Bundle bundle) {
        Object[] objs = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[objs.length];
        for (int i = 0; i < objs.length; i++) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i], format);
            } else {
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
            }
        }
        return messages;
    }
}
