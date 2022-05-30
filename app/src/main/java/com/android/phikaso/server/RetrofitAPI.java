package com.android.phikaso.server;

import com.android.phikaso.model.KakaoFriendsModel;
import com.android.phikaso.model.PhishingModel;
import com.android.phikaso.model.ReportModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @GET("/")
    Call<PhishingModel> getPhishingData(@Query("text") String text);

    // 사용자가 피싱 신고 등록 -> insert into DB
    @POST("/reports")
    Call<ReportModel> postPhishingMsg(@Body ReportModel reportModel);

    @Headers("Authorization: Bearer H3Nlcr08HZE4g1H0WJI8Z_JDvmml8nMt2OTLQYKeCilxIQAAAYEUo68H")
    @GET("/v1/api/talk/friends")
    Call<KakaoFriendsModel> getKakaoFriends(@Query("limit") int limit, @Query("order") String order);
}
