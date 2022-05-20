package com.android.phikaso.util;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;

import androidx.core.app.NotificationManagerCompat;

import java.util.List;
import java.util.Set;

public class PermissionUtil {
    // 알림 접근 권한이 있는지 확인하는 함수
    public static boolean checkNotificationPermission(final Context context) {
        final Set<String> sets = NotificationManagerCompat.getEnabledListenerPackages(context);
        return sets.contains(context.getApplicationContext().getPackageName());
    }

    // 접근성 권한이 있는지 확인하는 함수
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

    // 다른 앱 위에 표시 권한이 있는지 확인하는 함수
    public static boolean checkOverlayPermission(final Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Settings.canDrawOverlays(context);
    }
}
