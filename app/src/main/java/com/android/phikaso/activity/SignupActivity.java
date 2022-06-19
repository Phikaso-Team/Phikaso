package com.android.phikaso.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.phikaso.misc.ProgressDialog;
import com.android.phikaso.R;
import com.android.phikaso.model.UserModel;
import com.android.phikaso.util.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth     mAuth;
    private FirebaseDatabase mDatabase;
    ProgressDialog customProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 초기화
        mAuth     = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        findViewById(R.id.buttonSignUp).setOnClickListener(this); // 회원가입 버튼
        ((TextView) findViewById(R.id.service_text_terms)).setOnClickListener(this);// 이용약관 텍스트

        ((TextView) findViewById(R.id.privacy_text_terms)).setOnClickListener(this);// 개인정보처리방침 텍스트


        //로딩창 객체 생성
        customProgressDialog = new ProgressDialog(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonSignUp) {
            signUp();
            customProgressDialog.show();
        } else if(id == R.id.service_text_terms){
            Intent intentSearch = new Intent(SignupActivity.this, ServiceTermsActivity.class);
            startActivity(intentSearch);

        }
        else if(id == R.id.privacy_text_terms){
            Intent intentSearch = new Intent(SignupActivity.this, PrivacyTermsActivity.class);
            startActivity(intentSearch);

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
