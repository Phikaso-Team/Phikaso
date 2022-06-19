package com.phikaso.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.phikaso.app.R;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, PermissionActivity.class);
            startActivity(intent);
            SplashActivity.this.finish();
        }, SPLASH_DISPLAY_TIME);
    }
}
