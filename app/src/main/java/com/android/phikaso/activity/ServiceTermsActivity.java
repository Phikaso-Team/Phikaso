package com.android.phikaso.activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.android.phikaso.R;

public class ServiceTermsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_terms);
        findViewById(R.id.service_btn_back).setOnClickListener(this); // 닫기 버튼

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.service_btn_back) {
            onBackPressed();
        }
    }

}
