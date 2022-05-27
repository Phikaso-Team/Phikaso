package com.android.phikaso.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PreferenceManager {
    public static  final String  PREFERENCES_NAME = "main_preference";
    private static final String  DEFAULT_VALUE_STRING  = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int     DEFAULT_VALUE_INT     = 0;
    private static final long    DEFAULT_VALUE_LONG    = 0L;
    private static final float   DEFAULT_VALUE_FLOAT   = 0F;

    private static SharedPreferences getPreferences(final Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static void setString(final Context context, final String key, final String value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void setBoolean(final Context context, final String key, boolean value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void setInt(final Context context, final String key, int value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void setLong(final Context context, final String key, long value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void setFloat(final Context context, final String key, float value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static String getString(final Context context, final String key) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getString(key, DEFAULT_VALUE_STRING);
    }

    public static boolean getBoolean(final Context context, final String key) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN);
    }

    public static int getInt(final Context context, final String key) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getInt(key, DEFAULT_VALUE_INT);
    }

    public static long getLong(final Context context, final String key) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getLong(key, DEFAULT_VALUE_LONG);
    }

    public static float getFloat(final Context context, final String key) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getFloat(key, DEFAULT_VALUE_FLOAT);
    }

    public static void removeKey(final Context context, final String key) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(key);
        edit.apply();
    }

    public static void clear(final Context context) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.apply();
    }


    private static String getDateString() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(new Date());
    }
    public static int getPreventCountAll(final Context context) {
        return getInt(context, "count-all");
    }
    public static int getPreventCountToday(final Context context) {
        return getInt(context, "count-" + getDateString());
    }
    public static void increasePreventCount(final Context context) {
        setInt(context, "count-all", getPreventCountAll(context) + 1);
        setInt(context, "count-" + getDateString(), getPreventCountToday(context) + 1);
    }
}