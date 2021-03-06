package com.phikaso.app.activity;

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
            final List<String> denieds = PermissionUtil.getDeniedPermission(PermissionActivity.this);
            if (!denieds.isEmpty()) {
                ActivityCompat.requestPermissions(PermissionActivity.this, denieds.toArray(new String[0]), MULTIPLE_PERMISSIONS);
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
        if (permNoti) { // ?????? ?????? ??????
            views.imgNoti.setColorFilter(colorAllow);
            views.txtNoti.setTextColor(colorAllow);
            views.txtNotiDesc.setTextColor(colorAllow);
        } else {
            views.imgNoti.setColorFilter(colorDeny);
            views.txtNoti.setTextColor(colorDeny);
            views.txtNotiDesc.setTextColor(colorDesc);
        }
        if (permAccess) { // ????????? ?????? (?????? ??????)
            views.imgAccess.setColorFilter(colorAllow);
            views.txtAccess.setTextColor(colorAllow);
            views.txtAccessDesc.setTextColor(colorAllow);
        } else {
            views.imgAccess.setColorFilter(colorDeny);
            views.txtAccess.setTextColor(colorDeny);
            views.txtAccessDesc.setTextColor(colorDesc);
        }
        if (permOverlay) { // ?????? ??? ?????? ?????? ??????
            views.imgOverlay.setColorFilter(colorAllow);
            views.txtOverlay.setTextColor(colorAllow);
            views.txtOverlayDesc.setTextColor(colorAllow);
        } else {
            views.imgOverlay.setColorFilter(colorDeny);
            views.txtOverlay.setTextColor(colorDeny);
            views.txtOverlayDesc.setTextColor(colorDesc);
        }
    }

    private void requestPermissions() {
        boolean permNoti    = PermissionUtil.checkNotificationPermission(this);
        boolean permAccess  = PermissionUtil.checkAccessibilityPermission(this);
        boolean permOverlay = PermissionUtil.checkOverlayPermission(this);
        if (!permNoti) { // ?????? ?????? ??????
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            return;
        }
        if (!PermissionUtil.isMyServiceRunning(this, MyNotificationService.class)) {
            final Intent intent = new Intent(getApplicationContext(), MyNotificationService.class);
            startService(intent); // ????????? ??????
        }
        if (!permAccess) { // ????????? ?????? (?????? ??????)
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }
        if (!permOverlay) { // ?????? ??? ?????? ?????? ??????
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
