package com.phikaso.app.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.phikaso.app.R;
import com.phikaso.app.adapter.RecyclerViewAdapter;
import com.phikaso.app.model.FriendModel;
import com.phikaso.app.util.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kakao.sdk.talk.TalkApiClient;
import com.kakao.sdk.talk.model.Friend;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerAdapter;

    private ArrayList<FriendModel> setFriendList = new ArrayList<>();
    private ArrayList<FriendModel> getFriendList = new ArrayList<>();
    private Gson gson = new GsonBuilder().create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setFriendList();

        String value = PreferenceManager.getString(SettingActivity.this, "friend-list");
        getFriendList = gson.fromJson(value, new TypeToken<ArrayList<FriendModel>>() {}.getType());
        setRecyclerView(getFriendList);
    }

    // 리사이클러뷰 (실시간 보호 제외 사용자 리스트)
    private void setRecyclerView(ArrayList<FriendModel> mFriendList) {
        mRecyclerView = (RecyclerView) findViewById(R.id.kakao_friends_list);
        mRecyclerAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerAdapter.setFriendList(mFriendList);
    }

    // 카카오톡 친구 목록 가져오기 (실시간 보호 제외 사용자 저장)
    private void setFriendList() {
        TalkApiClient.getInstance().friends((friends, error) -> {
            if (error != null) {
                Log.e(TAG, "카카오톡 친구 목록 가져오기 실패", error);
            } else {
                Log.d(TAG, "카카오톡 친구 목록 가져오기 성공\n" + friends.getElements());

                if(friends.getElements().size() != 0) {
                    for (int i = 0; i < friends.getElements().size(); i++) {
                        Friend friend = friends.getElements().get(i);
                        String profile_thumbnail_image = friend.getProfileThumbnailImage();
                        String profile_nickname = friend.getProfileNickname();
                        setFriendList.add(new FriendModel(profile_thumbnail_image, profile_nickname));

                        PreferenceManager.setArrayList(SettingActivity.this, "friend-list", setFriendList);
                    }
                }
            }
            return null;
        });
    }
}