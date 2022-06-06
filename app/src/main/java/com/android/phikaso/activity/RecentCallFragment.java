package com.android.phikaso.activity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.phikaso.R;
import com.android.phikaso.model.CallItem;
import com.android.phikaso.util.RecyclerViewDecoration;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecentCallFragment extends Fragment {

    private ArrayList<CallItem> callList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CallRecyclerAdapter callAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recent_call, container, false);

        // RecyclerView
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        // 상하 여백 설정
        RecyclerViewDecoration itemDeco = new RecyclerViewDecoration(80);
        recyclerView.addItemDecoration(itemDeco);

        callAdapter = new CallRecyclerAdapter(callList);
        RecyclerView.LayoutManager layoutMgr = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutMgr);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(callAdapter);

        return v;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setData();
    }


    private void setData() {

        callList.add(new CallItem("010-0000-0000", "최선오", "21:00", "received"));
//        callList.add(new CallItem("010-1234-5678", "sunoh", "22:00", "missed"));
//        callList.add(new CallItem("010-0000-0000", "최선오", "21:00", "received"));
//        callList.add(new CallItem("010-1234-5678", "sunoh", "22:00", "missed"));
//        callList.add(new CallItem("010-0000-0000", "최선오", "21:00", "received"));
//        callList.add(new CallItem("010-1234-5678", "sunoh", "22:00", "missed"));
//        callList.add(new CallItem("010-0000-0000", "최선오", "21:00", "received"));
//        callList.add(new CallItem("010-1234-5678", "sunoh", "22:00", "missed"));
//        callList.add(new CallItem("010-0000-0000", "최선오", "21:00", "received"));
//        callList.add(new CallItem("010-1234-5678", "sunoh", "22:00", "missed"));
//        callList.add(new CallItem("010-0000-0000", "최선오", "21:00", "received"));
//        callList.add(new CallItem("010-1234-5678", "sunoh", "22:00", "missed"));
//        callList.add(new CallItem("010-0000-0000", "최선오", "21:00", "received"));
//        callList.add(new CallItem("010-1234-5678", "sunoh", "22:00", "missed"));


        String callData = getCallHistory();
        Log.d("ttt", callData);
    }


    // 통화 기록 가져오기
    // Todo: String -> ArrayList<CallItem> 으로 바꾸기
    private String getCallHistory() {

        String[] call_history = new String[] { CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.TYPE };

        // 보안상 Content Provider를 통해 URI로 접근해야 한다.
        Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, call_history, null, null, null);
        if (cursor.getCount() == 0) {
            return "최근 통화 기록이 없습니다";
        }
        cursor.moveToFirst();

        StringBuffer callBuff = new StringBuffer();
        do{
            int callData = cursor.getInt(0);
            SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-mm-dd");
            String date = myDateFormat.format(new Date(callData));
            callBuff.append(date + ":");

            // INCOMING_TYPE : 수신 | OUTGOING_TYPE : 발신 | MISSED_TYPE : 부재중 | VOICEMAIL_TYPE : 음성사서함 | REJECTED_TYPE : 거절
            if (cursor.getInt(1) == CallLog.Calls.INCOMING_TYPE)          callBuff.append("수신 :");
            else if (cursor.getInt(1) == CallLog.Calls.OUTGOING_TYPE)     callBuff.append("발신 :");
            else if (cursor.getInt(1) == CallLog.Calls.MISSED_TYPE)       callBuff.append("부재중 : ");
            else if (cursor.getInt(1) == CallLog.Calls.VOICEMAIL_TYPE)    callBuff.append("음성사서함 : ");
            else if (cursor.getInt(1) == CallLog.Calls.REJECTED_TYPE)     callBuff.append("수신거절 : ");
            else                                                             callBuff.append("알수없음 : ");
            callBuff.append(cursor.getString(2) + " | ");
            callBuff.append("\n");
        } while(cursor.moveToNext());
        cursor.close();

        return callBuff.toString();
    }

}