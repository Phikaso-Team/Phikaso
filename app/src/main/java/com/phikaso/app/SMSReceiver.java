package com.phikaso.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import java.util.Date;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSReceiver";

    // 문자가 수신되면 실행되는 메소드
    @Override
    public void onReceive(Context context, Intent intent) {
        // 수신 테스트
        Log.d(TAG, "onReceive() called ok");

        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = parseSmsMessage(bundle);

        // 아래에서 content만 사용해서 피싱검사하면 됌
        if (messages != null && messages.length > 0) {
            Log.d(TAG, "onReceive() receive sms ok");

            String sender = messages[0].getOriginatingAddress();
            Log.d(TAG, "onReceive() 보낸사람: " + sender);

            Date date = new Date(messages[0].getTimestampMillis());
            Log.d(TAG, "onReceive() 받은시간: " + date);

            String content = messages[0].getMessageBody();
            Log.d(TAG, "onReceive() 내용: " + content);

            String s = content;
            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
        }
    }

    private SmsMessage[] parseSmsMessage(Bundle bundle) {
        Object[] objs = (Object[]) bundle.get("puds");
        SmsMessage[] messages = new SmsMessage[objs.length];

        for (int i=0; i<objs.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
        }
        return messages;
    }
}