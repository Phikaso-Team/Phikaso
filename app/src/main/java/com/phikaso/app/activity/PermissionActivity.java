package com.phikaso.app.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.phikaso.app.R;
import com.phikaso.app.databinding.ActivityPermissionBinding;
import com.phikaso.app.service.MyNotificationService;
import com.phikaso.app.util.PermissionUtil;

import java.util.List;

public class PermissionActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "PermissionActivity";
    private static final int MULTIPLE_PERMISSIONS = 101;

    private ActivityPermissionBinding views;
    private int colorAllow, colorDeny, colorDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityPermissionBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        colorAllow = ContextCompat.getColor(this, R.color.perm_allow);
        colorDeny  = ContextCompat.getColor(this, R.color.perm_deny);
        colorDesc  = ContextCompat.getColor(this, R.color.perm_desc);
        views.permBtnRequest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.perm_btn_request) {
            final List<String> deniedPermList = PermissionUtil.getDeniedPermission(PermissionActivity.this);
            if (!deniedPermList.isEmpty()) {
                ActivityCompat.requestPermissions(PermissionActivity.this, deniedPermList.toArray(new String[0]), MULTIPLE_PERMISSIONS);
            } else {
                requestPermissions();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean permNoti    = PermissionUtil.checkNotificationPermission(this);
        boolean permAccess  = PermissionUtil.checkAccessibilityPermission(this);
        boolean permOverlay = PermissionUtil.checkOverlayPermission(this);
        if (permNoti && permAccess && permOverlay) {
            startActivity(new Intent(PermissionActivity.this, LoginActivity.class));
            finish();
            return;
        }
        if (permNoti) { // 알림 읽기 권한
            views.imgNoti.setColorFilter(colorAllow);
            views.txtNoti.setTextColor(colorAllow);
            views.txtNotiDesc.setTextColor(colorAllow);
        } else {
            views.imgNoti.setColorFilter(colorDeny);
            views.txtNoti.setTextColor(colorDeny);
            views.txtNotiDesc.setTextColor(colorDesc);
        }
        if (permAccess) { // 접근성 권한 (화면 읽기)
            views.imgAccess.setColorFilter(colorAllow);
            views.txtAccess.setTextColor(colorAllow);
            views.txtAccessDesc.setTextColor(colorAllow);
        } else {
            views.imgAccess.setColorFilter(colorDeny);
            views.txtAccess.setTextColor(colorDeny);
            views.txtAccessDesc.setTextColor(colorDesc);
        }
        if (permOverlay) { // 다른 앱 위에 표시 권한
            views.imgOverlay.setColorFilter(colorAllow);
            views.txtOverlay.setTextColor(colorAllow);
            views.txtOverlayDesc.setTextColor(colorAllow);
        } else {
            views.imgOverlay.setColorFilter(colorDeny);
            views.txtOverlay.setTextColor(colorDeny);
            views.txtOverlayDesc.setTextColor(colorDesc);
        }
        final List<String> deniedPermList = PermissionUtil.getDeniedPermission(PermissionActivity.this);
        if (!deniedPermList.contains(Manifest.permission.READ_CALL_LOG)) {
            views.imgCall.setColorFilter(colorAllow);
            views.txtCall.setTextColor(colorAllow);
            views.txtCallDesc.setTextColor(colorAllow);
        } else {
            views.imgCall.setColorFilter(colorDeny);
            views.txtCall.setTextColor(colorDeny);
            views.txtCallDesc.setTextColor(colorDeny);
        }
        if (!deniedPermList.contains(Manifest.permission.READ_CONTACTS)) {
            views.imgContact.setColorFilter(colorAllow);
            views.txtContact.setTextColor(colorAllow);
            views.txtContactDesc.setTextColor(colorAllow);
        } else {
            views.imgContact.setColorFilter(colorDeny);
            views.txtContact.setTextColor(colorDeny);
            views.txtContactDesc.setTextColor(colorDeny);
        }
        if (!deniedPermList.contains(Manifest.permission.RECEIVE_SMS)) {
            views.imgSms.setColorFilter(colorAllow);
            views.txtSms.setTextColor(colorAllow);
            views.txtSmsDesc.setTextColor(colorAllow);
        } else {
            views.imgSms.setColorFilter(colorDeny);
            views.txtSms.setTextColor(colorDeny);
            views.txtSmsDesc.setTextColor(colorDeny);
        }
    }

    private void requestPermissions() {
        boolean permNoti    = PermissionUtil.checkNotificationPermission(this);
        boolean permAccess  = PermissionUtil.checkAccessibilityPermission(this);
        boolean permOverlay = PermissionUtil.checkOverlayPermission(this);
        if (!permNoti) { // 알림 읽기 권한
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            return;
        }
        if (!PermissionUtil.isMyServiceRunning(this, MyNotificationService.class)) {
            final Intent intent = new Intent(getApplicationContext(), MyNotificationService.class);
            startService(intent); // 서비스 시작
        }
        if (!permAccess) { // 접근성 권한 (화면 읽기)
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }
        if (!permOverlay) { // 다른 앱 위에 표시 권한
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MULTIPLE_PERMISSIONS) {
            requestPermissions();
        }
    }
}
