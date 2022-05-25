package com.android.phikaso.model;

import com.google.gson.annotations.SerializedName;

// DTO : 사용자가 피싱 신고 -> store in DB
public class ReportModel {

    @SerializedName("kakaoMsg")
    private String kakaoMsg;

    @Override
    public String toString() {
        return kakaoMsg;
    }

    public String getKakaoMsg() {
        return kakaoMsg;
    }

    public void setKakaoMsg(String kakaoMsg) {
        this.kakaoMsg = kakaoMsg;
    }

    public ReportModel(String kakaoMsg) {
        this.kakaoMsg = kakaoMsg;
    }
}

