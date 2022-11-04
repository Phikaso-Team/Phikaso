package com.phikaso.app.activity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.phikaso.app.R;

public class ServiceTermsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_terms);
        findViewById(R.id.service_btn_back).setOnClickListener(this); // 닫기 버튼

        TextView term = findViewById(R.id.sterm_txt_content);
        term.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.service_btn_back) {
            onBackPressed();
        }
    }

}
