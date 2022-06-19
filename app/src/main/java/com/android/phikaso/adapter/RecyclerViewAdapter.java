package com.android.phikaso.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.phikaso.R;
import com.android.phikaso.model.FriendModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<FriendModel> mFriendList;

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kakao_friends_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        FriendModel friend = mFriendList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(friend.get_profile_thumbnail_image())
                .into(holder.profile_thumbnail_image);
        holder.profile_nickname.setText(friend.get_profile_nickname());
    }

    @Override
    public int getItemCount() {
        if(mFriendList == null) {
            return 0;
        }
        return mFriendList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_thumbnail_image;
        TextView profile_nickname;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_thumbnail_image = itemView.findViewById(R.id.profile_thumbnail_image);
            profile_nickname =  itemView.findViewById(R.id.profile_nickname);
        }
    }

    public void setFriendList(ArrayList<FriendModel> mFriendList) {
        this.mFriendList = mFriendList;
        notifyDataSetChanged();
    }
}