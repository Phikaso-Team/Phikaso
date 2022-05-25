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
    public void reportKakaoMsg(String text) {
        RetrofitAPI retrofitAPI = RetrofitClient.getClient().create(RetrofitAPI.class);
        ReportModel reportModel = new ReportModel(text);

        Call<Void> call = retrofitAPI.getKakaoMsg(reportModel);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                    return;
                }
                Log.d(TAG, String.valueOf(response.code()));

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

}
