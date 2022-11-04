package com.phikaso.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.phikaso.app.R;

public class PrivacyTermsActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_terms);
        findViewById(R.id.privacy_btn_back).setOnClickListener(this); // 닫기 버튼

        TextView term = findViewById(R.id.pterm_txt_content);
        term.setMovementMethod(new ScrollingMovementMethod());
        term.setText(Html.fromHtml(getString(R.string.privacy_term).replace("\n", "<br>")));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.privacy_btn_back) {
            onBackPressed();
        }
    }
}
