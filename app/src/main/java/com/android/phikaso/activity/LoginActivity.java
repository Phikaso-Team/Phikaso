package com.android.phikaso.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.phikaso.ProgressDialog;
import com.android.phikaso.R;
import com.android.phikaso.model.UserModel;
import com.android.phikaso.util.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private FirebaseAuth     mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDBReference;

    private View kakaoLogin, kakaoUnlink;
    ProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth     = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) { // 이미 로그인되어 있다면 메인 액티비티로 전환
            showMainScreen();
            return;
        }

        findViewById(R.id.login_btn_login).setOnClickListener(this);
        findViewById(R.id.login_text_register).setOnClickListener(this);
        ((TextView) findViewById(R.id.login_text_register)).setText(Html.fromHtml(getString(R.string.register)));

        kakaoLogin = findViewById(R.id.login_btn_kakao);
        kakaoUnlink = findViewById(R.id.kakaoUnlink);

        kakaoLogin();
        checkAgreeFriends();
        getAgreeFriends();

        //로딩창 객체 생성
        customProgressDialog = new ProgressDialog(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_btn_login) {
            firebaseLogin();
            customProgressDialog.show();


        } else if (id == R.id.login_text_register) {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        }
    }

    private void showMainScreen() {
        final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void firebaseLogin() {
        final String email    = ((EditText) findViewById(R.id.login_edit_email)).getText().toString();
        final String password = ((EditText) findViewById(R.id.login_edit_password)).getText().toString();

        if (email.length() == 0 || password.length() == 0) {
            showToast("이메일 또는 비밀번호를 입력해주세요.");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        if (task.getException() != null) {
                            showToast("이메일 또는 비밀번호가 일치하지 않습니다.");
                            return;
                        }
                    }
                    final FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        final String uid = user.getUid();
                        mDBReference = mDatabase.getReference().child("users");
                        mDBReference.child(uid)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NotNull DataSnapshot snapshot) {
                                        String name = snapshot.child("name").getValue(String.class);
                                        PreferenceManager.setString(LoginActivity.this, "personal-name", name);
                                        PreferenceManager.setString(LoginActivity.this, "personal-id", uid);
                                    }
                                    @Override
                                    public void onCancelled(@NotNull DatabaseError error) { }
                                });
                    }
                    showMainScreen(); // 메인 액티비티로 전환
                });
    }

    private void kakaoLogin() {
        // 카카오가 설치되어 있는지 확인
        Function2<OAuthToken, Throwable, Unit> callback = (oAuthToken, throwable) -> {
            // 토큰이 전달 되면 로그인 성공, 토큰이 전달되지 않았다면 로그인 실패
            if (oAuthToken != null) {
                Log.d("[카카오] 로그인", "성공");
                updateKakaoLogin();
            }
            if (throwable != null) {
                Log.d("[카카오] 로그인", "실패");
                Log.e("signInKakao()", throwable.getLocalizedMessage());
            }
            return null;
        };

        // 카카오톡 로그인
        kakaoLogin.setOnClickListener(view -> {
            if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
            } else {
                UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
            }
        });

        // 카카오톡 연결 끊기(테스트용)
        kakaoUnlink.setOnClickListener(view -> UserApiClient.getInstance().unlink(throwable -> {
            Log.d("kakaoUnlink", "연결 끊기 성공. SDK에서 토큰 삭제");
            updateKakaoLogin();
            return null;
        }));
        updateKakaoLogin();
    }

    private void updateKakaoLogin() {
        UserApiClient.getInstance().me((user, throwable) -> {
            if (user != null) {//로그인
                Log.d(TAG, "invoke: id " + user.getId());
                Log.d(TAG, "invoke: email " + user.getKakaoAccount().getEmail());
                Log.d(TAG, "invoke: nickname " + user.getKakaoAccount().getProfile().getNickname());

                kakaoLogin.setVisibility(View.GONE);
                kakaoUnlink.setVisibility(View.VISIBLE);

                UserModel userModel = new UserModel();
                userModel.name = user.getKakaoAccount().getProfile().getNickname();
                userModel.email = user.getKakaoAccount().getEmail();
                userModel.uid = String.valueOf(user.getId());
                mDatabase.getReference().child("users").child(String.valueOf(user.getId())).setValue(userModel);

                PreferenceManager.setString(LoginActivity.this, "personal-name", user.getKakaoAccount().getProfile().getNickname());
                PreferenceManager.setString(LoginActivity.this, "personal-id", String.valueOf(user.getId()));

                showMainScreen(); // 메인 액티비티로 전환
            } else {//연결 끊기
                kakaoLogin.setVisibility(View.VISIBLE);
                kakaoUnlink.setVisibility(View.GONE);
            }
            return null;
        });
    }

    // '카카오 서비스 내 친구목록' 동의 내역 조회
    private void checkAgreeFriends() {
        List<String> scopes = Collections.singletonList("friends");

        UserApiClient.getInstance().scopes(scopes, (scopeInfo, error) -> {
            if (error != null) {
                Log.e(TAG, "동의 정보 확인 실패", error);
            }else if (scopeInfo != null) {
                Log.i(TAG, "동의 정보 확인 성공\n 현재 가지고 있는 동의 항목: " + scopeInfo);
                PreferenceManager.setBoolean(this, "friends-agree", scopeInfo.getScopes().get(0).getAgreed());
            }
            return null;
        });
    }

    // '카카오 서비스 내 친구목록' 항목 동의 받기
    private void getAgreeFriends() {
        UserApiClient.getInstance().me((user, meError) -> {
            if (meError != null) {
                Log.d(TAG, "사용자 정보 요청 실패" + meError);
            } else if (user != null) {
                List<String> scopes = new ArrayList<>(Arrays.asList("profile_nickname", "account_email"));

                boolean friendsAgree = PreferenceManager.getBoolean(this, "friends-agree");
                if (friendsAgree != true) {
                    scopes.add("friends");
                }

                Log.d(TAG, "요청 개수 " + scopes.size());
                if (scopes.size() > 0) {
                    Log.d(TAG, "사용자에게 추가 동의를 받아야 합니다.");

                    UserApiClient.getInstance().loginWithNewScopes(this, scopes, (oAuthToken, error) -> {
                        if (error != null) {
                            Log.d(TAG, "사용자 추가 동의 실패" + error);
                        } else {
                            // 사용자 정보 재요청
                            UserApiClient.getInstance().me((newUser, newError) -> {
                                if (newError != null) {
                                    Log.d(TAG, "사용자 정보 요청 실패" + newError);
                                } else if (newUser != null) {
                                    Log.d(TAG, "사용자 정보 요청 성공");
                                }
                                return null;
                            });
                        }
                        return null;
                    });
                }
            }
            return null;
        });
    }

    private void showToast(final String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
