package com.android.phikaso.model;

import java.io.Serializable;

public class FriendModel implements Serializable {

    // 사용자 프로필 닉네임
    String profile_nickname;

    // 사용자 프로필 썸네일 이미지
    String profile_thumbnail_image;

    public FriendModel(String profile_thumbnail_image, String profile_nickname){
        this.profile_thumbnail_image = profile_thumbnail_image;
        this.profile_nickname = profile_nickname;
    }

    public String get_profile_nickname() {
        return profile_nickname;
    }

    public String get_profile_thumbnail_image() {
        return profile_thumbnail_image;
    }
}
