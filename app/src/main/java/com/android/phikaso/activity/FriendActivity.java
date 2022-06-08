package com.android.phikaso.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.phikaso.model.FriendModel;
import com.kakao.sdk.friend.client.PickerClient;
import com.kakao.sdk.friend.model.OpenPickerFriendRequestParams;
import com.kakao.sdk.friend.model.PickerOrientation;
import com.kakao.sdk.friend.model.PickerServiceTypeFilter;
import com.kakao.sdk.friend.model.SelectedUser;
import com.kakao.sdk.friend.model.ViewAppearance;

import java.util.ArrayList;

public class FriendActivity  extends AppCompatActivity {

    private static final String TAG = "FriendActivity";

    private ArrayList<FriendModel> mFriendList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickerFriend();
    }

    // 카카오톡 멀티 친구 피커 (실시간 보호 제외 사용자 선택)
    private void pickerFriend() {
        OpenPickerFriendRequestParams params = new OpenPickerFriendRequestParams(
                "실시간 보호 제외 사용자 선택", PickerServiceTypeFilter.TALK, ViewAppearance.AUTO, PickerOrientation.AUTO,
                true, true, false, true, true, 100, 1
        );

        PickerClient.getInstance().selectFriends(this, params, (selectedUsers, error) -> {
            if (error != null) {
                Log.e(TAG, "친구 선택 실패", error);

                Intent intent = new Intent(FriendActivity.this, SettingActivity.class);
                startActivity(intent);
            } else {
                Log.d(TAG, "친구 선택 성공\n" + selectedUsers.getUsers());

                if(selectedUsers.getUsers().size() != 0) {
                    for (int i = 0; i < selectedUsers.getUsers().size(); i++) {
                        SelectedUser friend = selectedUsers.getUsers().get(i);
                        String profile_thumbnail_image = friend.getProfileThumbnailImage();
                        String profile_nickname = friend.getProfileNickname();
                        mFriendList.add(new FriendModel(profile_thumbnail_image, profile_nickname));

                        Intent intent = new Intent();
                        intent.putExtra("friend_list", mFriendList);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            }
            return null;
        });
    }

}
