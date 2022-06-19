package com.phikaso.app.util;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PermissionUtil {
    public static final String[] permissions = {
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_CALL_LOG,
    };

    // 알림 접근 권한이 있는지 확인하는 메서드
    public static boolean checkNotificationPermission(final Context context) {
        final Set<String> sets = NotificationManagerCompat.getEnabledListenerPackages(context);
        return sets.contains(context.getApplicationContext().getPackageName());
    }

    // 접근성 권한이 있는지 확인하는 메서드
    public static boolean checkAccessibilityPermission(final Context context) {
        final AccessibilityManager manager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        final List<AccessibilityServiceInfo> list = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (int i = 0; i < list.size(); i++) {
            final AccessibilityServiceInfo info = list.get(i);
            if (info.getResolveInfo().serviceInfo.packageName.equals(context.getApplicationContext().getPackageName())) {
                return true;
            }
        }
        return false;
    }

    // 다른 앱 위에 표시 권한이 있는지 확인하는 메서드
    public static boolean checkOverlayPermission(final Context context) {
        return Settings.canDrawOverlays(context);
    }

    // 거부된 권한 리스트를 가져오는 메서드
    public static List<String> getDeniedPermission(final Context context) {
        final List<String> deniedList = new ArrayList<>();
        for (String pm : permissions) {
            if (ContextCompat.checkSelfPermission(context, pm) != PackageManager.PERMISSION_GRANTED) {
                deniedList.add(pm);
            }
        }
        return deniedList;
    }

    // 서비스가 실행 중인지 확인하는 메서드
    public static boolean isMyServiceRunning(final Context context, Class<?> serviceClass) {
        final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
