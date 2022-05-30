package com.android.phikaso.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KakaoFriendsModel {

    // 친구 리스트
    @SerializedName("elements")
    private List<FriendModel> elements;

    // 정보를 받을 수 있는 전체 친구 수
    @SerializedName("total_count")
    private int total_count;

    // 친구 리스트 중 즐겨찾기 친구 수
    @SerializedName("favorite_count")
    private int favorite_count;

    // 이전 페이지 URL, 이전 페이지가 없을 경우 null
    @SerializedName("before_url")
    private String before_url;

    // 다음 페이지 URL, 이전 페이지가 없을 경우 null
    @SerializedName("after_url")
    private String after_url;

    // 친구 목록 요청 결과 ID
    @SerializedName("result_id")
    private String result_id;

    public List<FriendModel> getElements(){
        return elements;
    }

    public void setElements(List<FriendModel> elements) {
        this.elements = elements;
    }
}
