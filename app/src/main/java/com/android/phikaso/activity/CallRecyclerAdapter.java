package com.android.phikaso.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.phikaso.R;
import com.android.phikaso.model.CallItem;

import java.util.ArrayList;


public class CallRecyclerAdapter extends RecyclerView.Adapter<CallRecyclerAdapter.CallViewHolder> {

    private ArrayList<CallItem> callList;

    public class CallViewHolder extends RecyclerView.ViewHolder {
        public TextView name;      // 이름
        public TextView number;    // 전화번호
        public TextView time;      // 시간
        public ImageView callType; // 전화 타입 (수신, 발신, 부재중)

        // View Holder
        public CallViewHolder(@NonNull View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.call_name);
            number = (TextView) view.findViewById(R.id.call_number);
            time = (TextView) view.findViewById(R.id.call_time);
            callType = (ImageView) view.findViewById(R.id.call_type);
        }
    }

    public CallRecyclerAdapter(ArrayList<CallItem> callData) {
        this.callList = callData;
    }

    @NonNull
    @Override
    public CallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_list_item, parent, false);
        return new CallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallRecyclerAdapter.CallViewHolder holder, int pos) {
        holder.name.setText(callList.get(pos).getName());
    }

    @Override
    public int getItemCount() {     // 화면에 보여줄 데이터의 개수
        return callList.size();
    }

}
