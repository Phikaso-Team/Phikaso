package com.android.phikaso.server;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @GET("/")
    Call<PhishingData> getPhishingData(@Query("text") String text);
}
