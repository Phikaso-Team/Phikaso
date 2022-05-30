package com.android.phikaso.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.phikaso.R;
import com.android.phikaso.RecyclerViewAdapter;
import com.android.phikaso.model.FriendModel;
import com.android.phikaso.model.KakaoFriendsModel;
import com.android.phikaso.server.RetrofitAPI;
import com.android.phikaso.server.RetrofitClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";
    private RecyclerViewAdapter recycler_view_adapter;
    private RecyclerView recycler_view;
    private ArrayList<FriendModel> friendModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        kakaoFriendsList();

        recycler_view = findViewById(R.id.recycler_view);
        friendModels = new ArrayList<>();

        recycler_view_adapter = new RecyclerViewAdapter(SettingActivity.this, friendModels);
        recycler_view.setAdapter(recycler_view_adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(SettingActivity.this, RecyclerView.VERTICAL,false));
    }

    // 카카오톡 친구 목록 가져오기
    public void kakaoFriendsList() {
        RetrofitAPI retrofitAPI = RetrofitClient.getKakaoApiClient().create(RetrofitAPI.class);
        Call<KakaoFriendsModel> call = retrofitAPI.getKakaoFriends(100, "asc");

        call.enqueue(new Callback<KakaoFriendsModel>() {
            @Override
            public void onResponse(Call<KakaoFriendsModel> call, Response<KakaoFriendsModel> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                    return;
                }

                KakaoFriendsModel data = response.body();
                FriendModel elements;

                String content = "";
                if (data != null && data.getElements().size() != 0) {
                    content += "elements: " + data.getElements() + "\n";
                    for (int i = 0; i < 100; i++) {
                        elements = data.getElements().get(i);
                        String profile_thumbnail_image = elements.get_profile_thumbnail_image();
                        String profile_nickname = elements.get_profile_nickname();
                        friendModels.add(new FriendModel(profile_thumbnail_image, profile_nickname));
                    }
                } else {
                    content += "No friends list";
                }

                Log.d(TAG, content);
            }

            @Override
            public void onFailure(Call<KakaoFriendsModel> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }
}
