package com.android.phikaso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.phikaso.model.FriendModel;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter {

    ArrayList<FriendModel> friendModels;
    Context context;

    public RecyclerViewAdapter(Context context, ArrayList<FriendModel> friendModels){
        this.friendModels = friendModels;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return friendModels.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kakao_friends_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;

        viewHolder.profile_thumbnail_image.setImageResource(Integer.parseInt(friendModels.get(position).get_profile_thumbnail_image()));
        viewHolder.profile_nickname.setText(friendModels.get(position).get_profile_nickname());
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
}
