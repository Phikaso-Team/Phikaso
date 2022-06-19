package com.android.phikaso.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.phikaso.R;
import com.android.phikaso.databinding.ActivityMainBinding;
import com.android.phikaso.databinding.ActivityPermissionBinding;
import com.android.phikaso.model.FriendModel;
import com.android.phikaso.service.MyOverlayService;
import com.android.phikaso.util.PermissionUtil;
import com.android.phikaso.util.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding views;
    private FirebaseDatabase    mDatabase;
    private DatabaseReference   mDBReference;
    private int count;
    private ArrayList<FriendModel> friendList = new ArrayList<>();
    private final Gson gson = new GsonBuilder().create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        mDatabase = FirebaseDatabase.getInstance();

        views.mainBtnSearch.setOnClickListener(this);  // 검색
        views.mainBtnAddCase.setOnClickListener(this);  // 신고
        views.mainBtnSetting.setOnClickListener(this);  // 설정
        views.phishingPrevent.setOnClickListener(this); // 피싱 예방

        views.mainSwProtection.setOnCheckedChangeListener(this);
        if (PreferenceManager.getString(MainActivity.this, "checked").equals("true")) {
            views.mainSwProtection.setChecked(true);
        } else {
            views.mainSwProtection.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) { // 뷰 클릭 이벤트
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
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) { // 실시간 보호 스위치
        if (isChecked) { // 팝업 알림 띄우기
            if (!PermissionUtil.isMyServiceRunning(this, MyOverlayService.class)) { // 서비스가 실행중인지 확인
                PreferenceManager.setString(MainActivity.this, "checked", "true");
                startService(new Intent(getApplicationContext(), MyOverlayService.class)); // 서비스 실행

                // 실시간 보호 제외 사용자
                String user = PreferenceManager.getString(MainActivity.this, "kakao-name");
                String value = PreferenceManager.getString(MainActivity.this, "friend-list");
                friendList = gson.fromJson(value, new TypeToken<ArrayList<FriendModel>>() {
                }.getType());

                for (int i = 0; i < friendList.size(); i++) {
                    if (friendList.get(i).get_profile_nickname().equals(user)) {
                        stopService(new Intent(getApplicationContext(), MyOverlayService.class));
                    }
                }
            }
        } else {
            PreferenceManager.setString(MainActivity.this, "checked", "false");
            stopService(new Intent(getApplicationContext(), MyOverlayService.class)); // 서비스 중지
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.getReference()
            .child("phishingCases")
            .child("count")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        count = (int) snapshot.getValue(Integer.class);
                    } else {
                        count = 0;
                    }
                    views.mainTextCntCase.setText(String.valueOf(count)); // 전체 피해 사례
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
    }

    @Override @SuppressLint("SetTextI18n")
    protected void onResume() {
        super.onResume();
        preventCountToday();
        preventCountAll();
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
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer count = snapshot.child("count").getValue(Integer.class);
                    if (count != null) {
                        views.mainCountToday.setText(String.valueOf(count));
                    } else {
                        views.mainCountToday.setText("0");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
    }

    //전체 피싱 예방
    private void preventCountAll() {
        mDBReference = FirebaseDatabase.getInstance().getReference();
        mDBReference.child("total")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer count = snapshot.child("count").getValue(Integer.class);
                    if (count != null) {
                        views.mainCountAll.setText(String.valueOf(count));
                    } else {
                        views.mainCountAll.setText("0");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
    }
}
