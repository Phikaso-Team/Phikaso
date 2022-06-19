package com.phikaso.app.model;

import com.google.gson.annotations.SerializedName;

// DTO : KakaoTalk Message
public class MsgModel {

    @SerializedName("kakaoMsg")
    private String kakaoMsg;

    @Override
    public String toString() {
        return kakaoMsg;
    }
}