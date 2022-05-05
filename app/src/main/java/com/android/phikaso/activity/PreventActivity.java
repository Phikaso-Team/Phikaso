package com.android.phikaso.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.android.phikaso.R;

import com.android.phikaso.util.PreferenceManager;

public class PreventActivity extends AppCompatActivity {
    private TextView personalName;
    private TextView personalCount;
    private TextView preventCountAll;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prevent);

        personalName = findViewById(R.id.personalName);
        personalCount = findViewById(R.id.personalCount);
        preventCountAll = findViewById(R.id.preventCountAll);

        personalName.setText(PreferenceManager.getString(this, "personal-name"));
        preventCountAll.setText(Integer.toString(PreferenceManager.getPreventCountAll(this)));
    }
}
