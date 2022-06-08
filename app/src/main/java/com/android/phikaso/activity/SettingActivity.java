package com.android.phikaso.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.phikaso.R;
import com.android.phikaso.RecyclerViewAdapter;
import com.android.phikaso.model.FriendModel;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerViewAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<FriendModel> mFriendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        findViewById(R.id.selectFriend).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.selectFriend) {
            Intent intent = new Intent(SettingActivity.this, FriendActivity.class);
            launcher.launch(intent);
        }
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        mFriendList = (ArrayList<FriendModel>) data.getSerializableExtra("friend_list");
                    }
                    setRecyclerView(mFriendList);
                }
            });

    private void setRecyclerView(ArrayList<FriendModel> mFriendList) {
        mRecyclerView = (RecyclerView) findViewById(R.id.kakao_friends_list);
        mRecyclerAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerAdapter.setFriendList(mFriendList);
    }

}