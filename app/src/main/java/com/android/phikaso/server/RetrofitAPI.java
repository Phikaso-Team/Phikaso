package com.android.phikaso.server;

import com.android.phikaso.model.PhishingModel;
import com.android.phikaso.model.ReportModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @GET("/")
    Call<PhishingModel> getPhishingData(@Query("text") String text);

    // 사용자가 피싱 신고 등록 -> insert into DB
    @POST("/reports")
    Call<ReportModel> postPhishingMsg(@Body ReportModel reportModel);
}
