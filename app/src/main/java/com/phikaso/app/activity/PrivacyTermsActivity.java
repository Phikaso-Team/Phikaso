package com.phikaso.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.phikaso.app.R;

public class PrivacyTermsActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_terms);
        findViewById(R.id.privacy_btn_back).setOnClickListener(this); // 닫기 버튼
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.privacy_btn_back) {
            onBackPressed();
        }
    }
}
