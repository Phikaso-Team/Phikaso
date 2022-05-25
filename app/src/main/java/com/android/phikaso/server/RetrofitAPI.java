package com.android.phikaso.server;

import com.android.phikaso.model.ReportModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface RetrofitAPI {
    @GET("/")
    Call<PhishingData> getPhishingData(@Query("text") String text);

    // 사용자가 피싱 신고 -> store in DB
    @POST("/reports")
    Call<Void> getKakaoMsg(@Body ReportModel reportModel);
}
