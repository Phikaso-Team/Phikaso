package com.android.phikaso.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.phikaso.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.buttonDelete).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonDelete:
                deleteUser();
                break;
        }
    }

    // 회원 탈퇴
    private void deleteUser() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this);
        dialog
            .setMessage("계정을 삭제할까요?")
            .setCancelable(false)
            .setPositiveButton("확인", (dialogInterface, i) -> {
                final FirebaseUser user = mAuth.getCurrentUser();
                user.delete().addOnCompleteListener(task -> {
                    Toast.makeText(SettingActivity.this, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_LONG).show();
                    finish();
                    final Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                });
            })
            .setNegativeButton("취소", (dialogInterface, i) -> { })
            .show();
    }
}
