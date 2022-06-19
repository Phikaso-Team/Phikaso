package com.android.phikaso.misc;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, "b0868f1437d5c86bb223e263b792f192");
    }
}
