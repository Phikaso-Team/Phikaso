package com.android.phikaso.activity;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
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

        setData(20);    // 최근 통화 기록 20개 데이터 가져오기
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
    }


    private void setData(int count) {

        callList.add(new CallItem("010-0000-0000", "최선오", "21:00", "received"));

        String callData = getCallHistory(count);

        BufferedReader br = new BufferedReader(new StringReader(callData));
        try {
            for (int i=0; i<count; i++) {
                String name = br.readLine();
                String number = br.readLine();
                String type = br.readLine();
                String date = br.readLine();
                callList.add(new CallItem(number, name, date, type));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // count 만큼 최근 통화 기록 가져오는 함수!
    private String getCallHistory(int count) {

        String[] call_history = new String[] {
                CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.TYPE };

        // 보안상 Content Provider를 통해 URI로 접근해야 한다.
        Cursor cursor = getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, call_history, null, null, null);
        if (cursor.getCount() == 0) {
            return "최근 통화 기록이 없습니다";
        }

        StringBuffer callBuff = new StringBuffer();

        cursor.moveToFirst();   // 가장 최근 목록부터 커서를 잡음
        for (int i=0; i<count; i++) {
            int nameIdx = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int numberIdx = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int typeIdx = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int dateIdx = cursor.getColumnIndex(CallLog.Calls.DATE);

            // (CACHED_NAME이 없는 = 연락처에 이름이 등록되지 않은) 사람 처리
            String name = cursor.getString(nameIdx);
            if (cursor.getString(nameIdx) == null) {
                name = "Unknown";
            }
            else {
                name = cursor.getString(nameIdx);
            }
            Log.d("tttName", name);
            callBuff.append(name + " |");

            String number = cursor.getString(numberIdx);
            Log.d("tttNUMBER", number);
            callBuff.append(number + " |");

            // INCOMING_TYPE : 수신 | OUTGOING_TYPE : 발신 | MISSED_TYPE : 부재중 | VOICEMAIL_TYPE : 음성사서함 | REJECTED_TYPE : 거절
            String type = cursor.getString(typeIdx);
            if (typeIdx == CallLog.Calls.INCOMING_TYPE)          callBuff.append("수신 +  |");
            else if (typeIdx == CallLog.Calls.OUTGOING_TYPE)     callBuff.append("발신 +  |");
            else if (typeIdx == CallLog.Calls.MISSED_TYPE)       callBuff.append("부재중 +  |");
            else if (typeIdx == CallLog.Calls.VOICEMAIL_TYPE)    callBuff.append("음성사서함 +  |");
            else if (typeIdx == CallLog.Calls.REJECTED_TYPE)     callBuff.append("수신거절 +  |");
            else                                                 callBuff.append("알수없음 +  |");
            Log.d("tttType", type);

            String date = cursor.getString(dateIdx);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd HH:mm");
            date = dateFormatter.format(new Date(Long.parseLong(date)));
            Log.d("tttDate", date);
            callBuff.append(date + " |");

            cursor.moveToNext();
        }
        cursor.close();

        Log.d("callBuff.toString()", callBuff.toString());
        return callBuff.toString();
    }

}
