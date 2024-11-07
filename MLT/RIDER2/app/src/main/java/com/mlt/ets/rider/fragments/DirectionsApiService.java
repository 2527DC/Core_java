package com.mlt.ets.rider.fragments;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DirectionsApiService {
    @GET("maps/api/directions/json")
    Call<ResponseBody> getDirections(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("key") String apiKey
    );
}
