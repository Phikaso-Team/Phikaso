package com.phikaso.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phikaso.app.R;
import com.phikaso.app.model.CallItem;

import java.util.ArrayList;

public class CallRecyclerAdapter extends RecyclerView.Adapter<CallRecyclerAdapter.CallViewHolder> {
    private final ArrayList<CallItem> callList;

    public class CallViewHolder extends RecyclerView.ViewHolder {
        public TextView  name;   // 이름
        public TextView  number; // 전화번호
        public TextView  date;   // 날짜 및 시간
        public ImageView type;   // 타입 (수신, 발신, 부재중)

        public CallViewHolder(@NonNull View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.call_name);
            number = (TextView) view.findViewById(R.id.call_number);
            date = (TextView) view.findViewById(R.id.call_date);
            type = (ImageView) view.findViewById(R.id.call_type);
        }
    }

    public CallRecyclerAdapter(ArrayList<CallItem> list) {
        this.callList = list;
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
        holder.number.setText(callList.get(pos).getNumber());
        holder.date.setText(callList.get(pos).getDate());

        String callType = callList.get(pos).getType();
        switch (callType) {
            case "1":   // 수신
                holder.type.setImageResource(R.drawable.ic_baseline_call_received_24);
                break;
            case "2":   // 발신
                holder.type.setImageResource(R.drawable.ic_baseline_call_made_24);
                break;
            case "3":   // 부재중
                holder.type.setImageResource(R.drawable.ic_baseline_call_missed_24);
                break;
            case "5":   // 거절
                holder.type.setImageResource(R.drawable.ic_baseline_phone_disabled_24);
                break;
            default:    // 기타
                holder.type.setImageResource(R.drawable.ic_baseline_error_outline_24);
                break;
        }

    }

    @Override
    public int getItemCount() {     // 화면에 보여줄 데이터의 개수
        return callList.size();
    }

}
