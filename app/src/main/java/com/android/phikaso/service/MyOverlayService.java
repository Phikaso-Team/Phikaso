package com.android.phikaso.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.phikaso.R;
import com.android.phikaso.model.PhishingModel;
import com.android.phikaso.server.RetrofitAPI;
import com.android.phikaso.server.RetrofitClient;
import com.android.phikaso.util.PreferenceManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOverlayService extends Service {
    private static final String TAG = "MyOverlayService";
    private View popupView;
    private DatabaseReference mDBReference;
    private Calendar calendar = Calendar.getInstance();
    private String today = calendar.get(Calendar.YEAR) + "-"
            + (calendar.get(Calendar.MONTH) + 1) + "-"
            + calendar.get(Calendar.DATE);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocalBroadcastManager.getInstance(this).registerReceiver( // 백그라운드
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String text = intent.getStringExtra(MyNotificationService.EXTRA_TEXT);
                        checkProbability(text);
                    }
                }, new IntentFilter(MyNotificationService.ACTION_NOTIFICATION_BROADCAST)
        );
        LocalBroadcastManager.getInstance(this).registerReceiver( // 포그라운드
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String text = intent.getStringExtra(MyAccessibilityService.EXTRA_TEXT);
                        checkProbability(text);
                    }
                }, new IntentFilter(MyAccessibilityService.ACTION_NOTIFICATION_BROADCAST)
        );
        return START_NOT_STICKY;
    }

    private void showPopup(String p) {
        if (this.popupView == null) {
            int flags;
            try {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                this.popupView = layoutInflater.inflate(R.layout.overdraw_popup, null);

                if (Build.VERSION.SDK_INT >= 26) { // 안드로이드 8 이상
                    flags = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else { // 안드로이드 8 미만
                    flags = WindowManager.LayoutParams.TYPE_PHONE;
                }
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        flags,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT
                );
                layoutParams.gravity = Gravity.CENTER;

                WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                windowManager.addView(popupView, layoutParams);
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        TextView prob = (TextView) popupView.findViewById(R.id.textViewProbability);
        prob.setText(p);

        Button btnClose = (Button) popupView.findViewById(R.id.buttonOk);
        btnClose.setOnClickListener(view -> hidePopup());

        updatePersonalCount(PreferenceManager.getString(this, "personal-id"));
        updateTodayCount(today);
        updateTotalCount();
    }

    private void hidePopup() {
        if (this.popupView != null) {
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.removeView(this.popupView);
            this.popupView = null;
        }
    }

    @Override
    public void onDestroy() {
        hidePopup();
        super.onDestroy();
    }

    public void checkProbability(final String text) {
        final RetrofitAPI retrofitAPI = RetrofitClient.getClient().create(RetrofitAPI.class);
        final Call<PhishingModel> call = retrofitAPI.checkPhishingProbability(text);
        call.enqueue(new Callback<PhishingModel>() {
            @Override
            public void onResponse(@NonNull Call<PhishingModel> call, @NonNull Response<PhishingModel> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, String.valueOf(response.code()));
                    return;
                }
                final PhishingModel data = response.body();
                if (data != null && data.getPhishing()) {
                    showPopup(data.getProbability());
                }
            }
            @Override
            public void onFailure(@NonNull Call<PhishingModel> call, @NonNull Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
            }
        });
    }

    // 개인별 피싱 예방 횟수
    private void updatePersonalCount(String uid) {
        mDBReference = FirebaseDatabase.getInstance().getReference().child("users");
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("" + uid + "/count/", ServerValue.increment(1));
        mDBReference.updateChildren(childUpdates);
    }

    // 오늘의 피싱 예방 횟수
    private void updateTodayCount(String today) {
        mDBReference = FirebaseDatabase.getInstance().getReference().child("total");
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("" + today + "/count/", ServerValue.increment(1));
        mDBReference.updateChildren(childUpdates);
    }

    // 전체 피싱 예방 횟수
    private void updateTotalCount() {
        mDBReference = FirebaseDatabase.getInstance().getReference().child("total");
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("" + "/count/", ServerValue.increment(1));
        mDBReference.updateChildren(childUpdates);
    }
}
