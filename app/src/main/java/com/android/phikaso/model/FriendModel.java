package com.android.phikaso.model;

import com.google.gson.annotations.SerializedName;

public class FriendModel {

    // 회원번호
    @SerializedName("id")
    private long id;

    // 사용자 고유 ID
    @SerializedName("uuid")
    private String uuid;

    // 사용자 프로필 닉네임
    @SerializedName("profile_nickname")
    private String profile_nickname;

    // 사용자 프로필 썸네일 이미지
    @SerializedName("profile_thumbnail_image")
    private String profile_thumbnail_image;

    // 카카오톡 즐겨찾기 여부
    @SerializedName("favorite")
    private boolean favorite;

    // 메시지 차단 여부, true(메시지 수신 허용) 또는 false(메시지 차단)
    @SerializedName("allowed_msg")
    private boolean allowed_msg;

    public String get_profile_nickname() {
        return profile_nickname;
    }

    public void set_profile_nickname(String profile_nickname) {
        this.profile_nickname = profile_nickname;
    }

    public String get_profile_thumbnail_image() {
        return profile_thumbnail_image;
    }

    public void set_profile_thumbnail_image(String profile_thumbnail_image) {
        this.profile_thumbnail_image = profile_thumbnail_image;
    }

    public FriendModel(){

    }

    public FriendModel(String profile_thumbnail_image, String profile_nickname){
        this.profile_thumbnail_image = profile_thumbnail_image;
        this.profile_nickname = profile_nickname;
    }
}
