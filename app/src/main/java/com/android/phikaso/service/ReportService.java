package com.android.phikaso.service;

import android.util.Log;

import com.android.phikaso.model.ReportModel;

import com.android.phikaso.server.RetrofitAPI;
import com.android.phikaso.server.RetrofitClient;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportService {

    private static final String TAG = "ReportService";

    // 사용자 피싱 신고
    public void reportPhishing(String text) {
        RetrofitAPI retrofitAPI = RetrofitClient.getClient().create(RetrofitAPI.class);
        sqlCheck(text);
        ReportModel reportModel = new ReportModel(text);

        Call<ReportModel> call = retrofitAPI.postPhishingMsg(reportModel);
        call.enqueue(new Callback<ReportModel>() {
            @Override
            public void onResponse(Call<ReportModel> call, Response<ReportModel> response) {
                if (response.isSuccessful()) {
                    ReportModel postResult = response.body();    // data
                    Log.d(TAG, "onResponse 성공 -> " + postResult.toString());
                    String phishingMsg = response.body().getPhishingReportMsg();
                } else {
                    Log.d(TAG, "onResponse 실패");
                }
//                if (!response.isSuccessful()) {
//                    Log.e("TAG", "response 33: " + new Gson().toJson(response.body()));
//                    Log.d(TAG, String.valueOf(response.code()));
//                    return;
//                }
            }

            @Override
            public void onFailure(Call<ReportModel> call, Throwable t) {
                // Log Error since request failed
                Log.d(TAG, "통신 실패: " + t.getMessage());
            }
        });
    }

    // prevent SQL Injection
    public void sqlCheck(String input) {

        // special character filtering
        input = input.replace("&","&amp;");
        input = input.replace("<","&lt;");
        input = input.replace(">","&gt;");
        input = input.replace("'","&#039");

        // query filtering
        input = input.toLowerCase().replace("select", "");
        input = input.toLowerCase().replace("insert", "");
        input = input.toLowerCase().replace("update", "");
        input = input.toLowerCase().replace("delete", "");
        input = input.toLowerCase().replace("drop", "");
        input = input.toLowerCase().replace("union", "");
    }

}