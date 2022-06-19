package com.phikaso.app.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.phikaso.app.R;
import com.phikaso.app.adapter.CallRecyclerAdapter;
import com.phikaso.app.model.CallItem;
import com.phikaso.app.util.RecyclerViewDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class RecentCallFragment extends Fragment {
    private ArrayList<CallItem> callList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CallRecyclerAdapter callAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recent_call, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);

        // 상하 여백 설정
        RecyclerViewDecoration itemDeco = new RecyclerViewDecoration(80);
        recyclerView.addItemDecoration(itemDeco);

        setData(20); // 최근 통화 기록 20개 데이터 가져오기
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

    // count 만큼 최근 통화 기록 데이터를 callList<CallItem>에 추가
    private void setData(int count) {
        String callData = getCallHistory(count);
        StringTokenizer st = new StringTokenizer(callData, "|");
        for (int i=0; i<count; i++) {
            String name = st.nextToken();
            String number = st.nextToken();
            String type = st.nextToken();
            Log.d("tttcallType", type);
            String date = st.nextToken();
            callList.add(new CallItem(number, name, date, type));
        }
    }

    // count 만큼 최근 통화 기록을 "|"로 구분된 String으로 가져오는 함수!
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
            callBuff.append(name + "|");

            String number = cursor.getString(numberIdx);
            Log.d("tttNUMBER", number);
            callBuff.append(number + "|");

            // INCOMING_TYPE : 수신 | OUTGOING_TYPE : 발신 | MISSED_TYPE : 부재중 | VOICEMAIL_TYPE : 음성사서함 | REJECTED_TYPE : 거절
            String type = cursor.getString(typeIdx);
//            if (typeIdx == CallLog.Calls.INCOMING_TYPE)          callBuff.append("수신" + "|");
//            else if (typeIdx == CallLog.Calls.OUTGOING_TYPE)     callBuff.append("발신" +  "|");
//            else if (typeIdx == CallLog.Calls.MISSED_TYPE)       callBuff.append("부재중" +  "|");
//            else if (typeIdx == CallLog.Calls.VOICEMAIL_TYPE)    callBuff.append("음성사서함" +  "|");
//            else if (typeIdx == CallLog.Calls.REJECTED_TYPE)     callBuff.append("수신거절" +  "|");
//            else                                                 callBuff.append("알수없음" +  "|");
            Log.d("tttType", type);
            callBuff.append(type + "|");

            String date = cursor.getString(dateIdx);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd HH:mm");
            date = dateFormatter.format(new Date(Long.parseLong(date)));
            Log.d("tttDate", date);
            callBuff.append(date + "|");

            cursor.moveToNext();
        }
        cursor.close();

        Log.d("callBuff.toString()", callBuff.toString());
        return callBuff.toString();
    }
}
