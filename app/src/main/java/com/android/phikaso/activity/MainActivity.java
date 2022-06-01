package com.android.phikaso.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.phikaso.R;
import com.android.phikaso.service.MyOverlayService;
import com.android.phikaso.util.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private FirebaseDatabase  mDatabase;
    private DatabaseReference mDBReference;
    private TextView textViewCount;
    private Switch switchProtection;
    private int count;

    private TextView preventCountToday;
    private TextView preventCountAll;
    ProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance();

        findViewById(R.id.main_btn_search).setOnClickListener(this);
        findViewById(R.id.main_btn_add_case).setOnClickListener(this);
        findViewById(R.id.main_btn_setting).setOnClickListener(this);

        textViewCount    = findViewById(R.id.main_text_cnt_case); // 전체 피해 사례
        switchProtection = findViewById(R.id.main_sw_protection); // 실시간 보호

        findViewById(R.id.phishingPrevent).setOnClickListener(this); // 피싱 예방
        preventCountToday = (TextView) findViewById(R.id.main_count_today);
        preventCountAll   = (TextView) findViewById(R.id.main_count_all);


        if (PreferenceManager.getString(MainActivity.this, "checked").equals("true")) {
            switchProtection.setChecked(true);
        } else {
            switchProtection.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.main_btn_search) { // 피싱 번호 조회
            Intent intentSearch = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intentSearch);
        } else if (id == R.id.main_btn_add_case) { // 피해 사례 등록
            Intent intentRegister = new Intent(MainActivity.this, ReportActivity.class);
            startActivity(intentRegister);
        } else if (id == R.id.phishingPrevent) { // 피싱 예방
            Intent intentPrevent = new Intent(MainActivity.this, PreventActivity.class);
            startActivity(intentPrevent);
        } else if (id == R.id.main_btn_setting) { // 설정
            Intent intentSetting = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intentSetting);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.getReference().child("phishingCases").child("count")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        count = (int) snapshot.getValue(Integer.class);
                    } else {
                        count = 0;
                    }
                    textViewCount.setText(String.valueOf(count));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        preventCountToday();
        preventCountAll();

        //실시간 보호
        switchProtection.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) { // 팝업 알림 띄우기
                if (!isMyServiceRunning(MyOverlayService.class)) {
                    PreferenceManager.setString(MainActivity.this, "checked", "true");
                    startService(new Intent(getApplicationContext(), MyOverlayService.class));
                }
            } else {
                PreferenceManager.setString(MainActivity.this, "checked", "false");
                stopService(new Intent(getApplicationContext(), MyOverlayService.class));
            }
        });
    }

    // 서비스가 실행 중인지 확인하는 함수
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // 오늘의 피싱 예방
    private void preventCountToday() {
        Calendar calendar = Calendar.getInstance();
        String today = calendar.get(Calendar.YEAR) + "-"
                + (calendar.get(Calendar.MONTH) + 1) + "-"
                + calendar.get(Calendar.DATE);

        mDBReference = FirebaseDatabase.getInstance().getReference();
        mDBReference.child("total").child(today)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Integer count = snapshot.child("count").getValue(Integer.class);
                    if (count != null) {
                        preventCountToday.setText(String.valueOf(count));
                    } else {
                        preventCountToday.setText("0");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
    }

    //전체 피싱 예방
    private void preventCountAll() {
        mDBReference = FirebaseDatabase.getInstance().getReference();
        mDBReference.child("total")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Integer count = snapshot.child("count").getValue(Integer.class);
                    if (count != null) {
                        preventCountAll.setText(String.valueOf(count));
                    } else {
                        preventCountAll.setText("0");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
    }
}
