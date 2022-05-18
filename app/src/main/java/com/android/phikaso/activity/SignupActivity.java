package com.android.phikaso.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.phikaso.R;
import com.android.phikaso.model.UserModel;
import com.android.phikaso.util.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth     mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 초기화
        mAuth     = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        findViewById(R.id.buttonSignUp).setOnClickListener(this); // 회원가입 버튼
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSignUp) {
            signUp();
        }
    }

    private void signUp() {
        final String name      = ((EditText) findViewById(R.id.editTextName)).getText().toString();
        final String email     = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
        final String password  = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
        final String password2 = ((EditText) findViewById(R.id.editTextPasswordCheck)).getText().toString();

        if(email.length() == 0 || password.length() == 0 || password2.length() == 0) {
            showToast("이메일 또는 비밀번호를 입력해주세요.");
            return;
        }
        if (!password.equals(password2)) {
            showToast("비밀번호가 일치하지 않습니다.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    final String uid = task.getResult().getUser().getUid();
                    UserModel userModel = new UserModel();
                    userModel.name = name;
                    userModel.email = email;
                    userModel.uid = uid;
                    mDatabase.getReference().child("users").child(uid).setValue(userModel);

                    PreferenceManager.setString(SignupActivity.this, "personal-name", name);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    if(task.getException()!=null) {
                        showToast(task.getException().toString());
                    }
                }
            });
    }

    private void showToast(final String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
