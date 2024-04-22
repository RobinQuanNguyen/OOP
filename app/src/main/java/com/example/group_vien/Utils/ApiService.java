package com.example.group_vien.Utils;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiService {
    @POST("synt/statfin_synt_pxt_12dy.px")
    Call<ResponseBody> getPopulation(
        @Body RequestBody jsonQuery
    );

    @POST("tyokay/statfin_tyokay_pxt_125s.px")
    Call<ResponseBody> getWorkplaceSs(
        @Body RequestBody jsonQuery
    );

    @POST("tyokay/statfin_tyokay_pxt_115x.px")
    Call<ResponseBody> getEmploymentRate(
        @Body RequestBody jsonQuery
    );

    @GET("synt/statfin_synt_pxt_12dy.px")
    Call<ResponseBody> getArea();


    @GET("geo/1.0/direct")
    Call<ResponseBody> getAreaLocation(@Query("q") String area,
                                       @Query("limit") String limit,
                                       @Query("appid") String apiKey);

    @GET("data/2.5/weather")
    Call<ResponseBody> getWeatherData(@Query("lat") String lat,
                                      @Query("lon") String lon,
                                      @Query("appid") String apiKey);

    @Headers({ "Content-Type: application/json"})
    @POST("v1/chat/completions")
    Call<ResponseBody> getQuestionData(@Header("Authorization") String token,
                                       @Body RequestBody jsonQuery);

}
