package com.android.phikaso.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.phikaso.R;
import com.android.phikaso.server.RetrofitAPI;
import com.android.phikaso.server.RetrofitClient;
import com.android.phikaso.server.PhishingData;
import com.android.phikaso.service.MyNotificationService;
import com.android.phikaso.service.MyOverlayService;
import com.android.phikaso.util.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AlertDialog dialog = null;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDBReference;
    private TextView textViewCount;
    private Switch switchProtection;
    private int count;

    private TextView preventCountToday;
    private TextView preventCountAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //초기화
        mDatabase= FirebaseDatabase.getInstance();

        findViewById(R.id.buttonSearch).setOnClickListener(onClickListener);//피싱 번호 조회
        findViewById(R.id.buttonRegister).setOnClickListener(onClickListener);//피해 사례 등록
        findViewById(R.id.buttonSetting).setOnClickListener(onClickListener);//설정
        textViewCount = findViewById(R.id.textViewCount);//전체 피해 사례
        switchProtection = findViewById(R.id.switchProtection);//실시간 보호

        findViewById(R.id.phishingPrevent).setOnClickListener(onClickListener);//피싱 예방
        preventCountToday = (TextView) findViewById(R.id.main_count_today);
        preventCountAll   = (TextView) findViewById(R.id.main_count_all);

        if(PreferenceManager.getString(MainActivity.this, "checked").equals("true")){
            switchProtection.setChecked(true);
        }else{
            switchProtection.setChecked(false);
        }

        //카카오톡 대화 읽어오기
//        LocalBroadcastManager.getInstance(this).registerReceiver(//백그라운드
//                new BroadcastReceiver() {
//                    @SuppressLint("SetTextI18n")
//                    @Override
//                    public void onReceive(Context context, Intent intent) {
//                        String text = intent.getStringExtra(MyNotificationService.EXTRA_TEXT);
//                        deepLearningServer(text);
//                    }
//                }, new IntentFilter(MyNotificationService.ACTION_NOTIFICATION_BROADCAST)
//        );
//        LocalBroadcastManager.getInstance(this).registerReceiver(//포그라운드
//                new BroadcastReceiver() {
//                    @SuppressLint("SetTextI18n")
//                    @Override
//                    public void onReceive(Context context, Intent intent) {
//                        String text = intent.getStringExtra(MyAccessibilityService.EXTRA_TEXT);
//                        deepLearningServer(text);
//                    }
//                }, new IntentFilter(MyAccessibilityService.ACTION_NOTIFICATION_BROADCAST)
//        );
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonSearch://피싱 번호 조회
                    Intent intentSearch = new Intent(getApplicationContext(), NumberSearchActivity.class);
                    startActivity(intentSearch);
                    break;
                case R.id.buttonRegister://피해 사례 등록
                    Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intentRegister);
                    break;
                case R.id.buttonSetting://설정
                    Intent intentSetting = new Intent(getApplicationContext(), SettingActivity.class);
                    startActivity(intentSetting);
                    break;
                case R.id.phishingPrevent://피싱 예방
                    Intent intentPrevent = new Intent(getApplicationContext(), PreventActivity.class);
                    startActivity(intentPrevent);
                    break;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.getReference().child("phishingCases").child("count")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    count = (int)snapshot.getValue(Integer.class);
                }else{
                    count = 0;
                }
                textViewCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        preventCountToday();
        preventCountAll();

        if (this.dialog != null) {
            this.dialog.dismiss();
        }

        if (!checkNotificationPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("알림 접근 권한 필요");
                builder.setMessage("알림 접근 권한이 필요합니다.");
                builder.setPositiveButton("설정", (dialog, which) -> {
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                });
                dialog = builder.create();
                dialog.show();
                return;
            }
        }

        if (!isMyServiceRunning(MyNotificationService.class)) {
            Intent intent = new Intent(getApplicationContext(), MyNotificationService.class);
            startService(intent); // 서비스 시작
            Toast.makeText(this.getApplicationContext(), "알림 읽기 서비스 - 시작됨", Toast.LENGTH_SHORT).show();
        }

        if (!checkAccessibilityPermission()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("접근성 권한 필요");
            builder.setMessage("접근성 권한이 필요합니다.\n\n설치된 앱 -> 허용");
            builder.setPositiveButton("설정", (dialog, which) -> {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            });
            dialog = builder.create();
            dialog.show();
            return;
        }

        if(!checkOverlayPermission()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("다른 앱 위에 표시 권한 필요");
                builder.setMessage("다른 앱 위에 표시 권한이 필요합니다.");
                builder.setPositiveButton("설정", (dialog, which) -> {
                    startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                });
                dialog = builder.create();
                dialog.show();
                return;
            }
        }

        //실시간 보호
        switchProtection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //팝업 알림 띄우기
                    if(!isMyServiceRunning(MyOverlayService.class)){
                        PreferenceManager.setString(MainActivity.this, "checked", "true");
                        startService(new Intent(getApplicationContext(), MyOverlayService.class));
                    }
                }else{
                    PreferenceManager.setString(MainActivity.this, "checked", "false");
                    stopService(new Intent(getApplicationContext(), MyOverlayService.class));
                }
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

    // 알림 접근 권한이 있는지 확인하는 함수
    public boolean checkNotificationPermission(){
        final Set<String> sets = NotificationManagerCompat.getEnabledListenerPackages(this);
        return sets.contains(getApplicationContext().getPackageName());
    }

    // 접근성 권한이 있는지 확인하는 함수
    public boolean checkAccessibilityPermission() {
        final AccessibilityManager manager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        final List<AccessibilityServiceInfo> list = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (int i = 0; i < list.size(); i++) {
            final AccessibilityServiceInfo info = list.get(i);
            if (info.getResolveInfo().serviceInfo.packageName.equals(getApplication().getPackageName())) {
                return true;
            }
        }
        return false;
    }

    // 다른 앱 위에 표시 권한이 있는지 확인하는 함수
    public boolean checkOverlayPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Settings.canDrawOverlays(this);
    }

    //딥러닝 서버
    public void deepLearningServer(String text){
        RetrofitAPI retrofitAPI = RetrofitClient.getClient().create(RetrofitAPI.class);
        Call<PhishingData> call = retrofitAPI.getPhishingData(text);//카카오톡에서 읽어오는 텍스트

        call.enqueue(new Callback<PhishingData>() {
            @Override
            public void onResponse(Call<PhishingData> call, Response<PhishingData> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                    return;
                }

                PhishingData data = response.body();

                String content = "";
                content += "Phishing: " + data.getPhishing() + "\n";
                if(data.getProbability().contains("%")){
                    data.getProbability().replace("%","");
                }
                content += "Probability: " + data.getProbability() + "\n";
                content += "Text: " + data.getText() + "\n";

//                if (data.getPhishing()) {
//                    Intent popup = new Intent(MainActivity.this, PopupActivity.class);
//                    popup.putExtra("probability", data.getProbability());
//                    popup.putExtra("is_phishing", data.getPhishing());
//                    popup.putExtra("text", data.getText());
//                    popup.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    popup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(popup);
//                }


                Log.d(TAG, content);
            }

            @Override
            public void onFailure(Call<PhishingData> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    //오늘의 피싱 예방
    private void preventCountToday(){
        Calendar calendar = Calendar.getInstance();
        String today = calendar.get(Calendar.YEAR) + "-"
                + (calendar.get(Calendar.MONTH)+1) + "-"
                + calendar.get(Calendar.DATE);

        mDBReference = FirebaseDatabase.getInstance().getReference();
        mDBReference.child("total").child(today)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Integer count = snapshot.child("count").getValue(Integer.class);
                        if(count != null){
                            preventCountToday.setText(String.valueOf(count));
                        }else{
                            preventCountToday.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }

    //전체 피싱 예방
    private void preventCountAll(){
        mDBReference = FirebaseDatabase.getInstance().getReference();
        mDBReference.child("total")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Integer count = snapshot.child("count").getValue(Integer.class);
                        if(count != null){
                            preventCountAll.setText(String.valueOf(count));
                        }else{
                            preventCountAll.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }
}