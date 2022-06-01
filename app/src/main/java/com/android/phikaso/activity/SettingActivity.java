package com.android.phikaso.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.phikaso.R;
import com.android.phikaso.RecyclerViewAdapter;
import com.android.phikaso.model.FriendModel;
import com.kakao.sdk.talk.TalkApiClient;
import com.kakao.sdk.talk.model.Friend;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";

    private RecyclerViewAdapter recycler_view_adapter;
    private RecyclerView recycler_view;
    private ArrayList<FriendModel> friendModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getFriendsList();

        recycler_view = findViewById(R.id.recycler_view);
        friendModels = new ArrayList<>();

        recycler_view_adapter = new RecyclerViewAdapter(SettingActivity.this, friendModels);
        recycler_view.setAdapter(recycler_view_adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(SettingActivity.this, RecyclerView.VERTICAL,false));
    }

    // 카카오톡 친구 목록 가져오기
    private void getFriendsList() {
        TalkApiClient.getInstance().friends((friends, error) -> {
            if (error != null) {
                Log.e(TAG, "카카오톡 친구 목록 가져오기 실패", error);
            }
            else if (friends != null) {
                Log.i(TAG, "카카오톡 친구 목록 가져오기 성공 \n" + friends.getElements() + "\n");
                if(friends.getElements().size() != 0) {
                    for (int i = 0; i < 100; i++) {
                        Friend friend = friends.getElements().get(i);
                        String profile_thumbnail_image = friend.getProfileThumbnailImage();
                        String profile_nickname = friend.getProfileNickname();
                        friendModels.add(new FriendModel(profile_thumbnail_image, profile_nickname));
                    }
                }
            }
            return null;
        });
    }
}
