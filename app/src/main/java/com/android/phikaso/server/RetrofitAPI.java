package com.android.phikaso.server;

import com.android.phikaso.model.PhishingModel;
import com.android.phikaso.model.ReportModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitAPI {
    // 피싱 확률 확인
    @GET("/check")
    Call<PhishingModel> checkPhishingProbability(@Query("text") String text);

    // 피싱 의심 신고
    @POST("/report")
    Call<ReportModel> reportPhishingMessage(@Body ReportModel reportModel);
}
