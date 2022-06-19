package com.phikaso.app.service;

import android.util.Log;

import com.phikaso.app.model.ReportModel;

import com.phikaso.app.server.RetrofitAPI;
import com.phikaso.app.server.RetrofitClient;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportService {
    private static final String TAG = "ReportService";

    // 사용자 피싱 신고
    public void reportPhishing(String text) {
        RetrofitAPI retrofitAPI = RetrofitClient.getClient().create(RetrofitAPI.class);
        ReportModel reportModel = new ReportModel(text);

        Call<ReportModel> call = retrofitAPI.reportPhishingMessage(reportModel);
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
            }

            @Override
            public void onFailure(Call<ReportModel> call, Throwable t) {
                Log.d(TAG, "통신 실패: " + t.getMessage());
            }
        });
    }
}
