package com.mlt.ets.rider.network;


import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // POST request for user sign-up
    @POST("register")
    Call<JSONObject> signUpUser(@Body RequestBody requestBody);

    @POST("/api/login")
    Call<JSONObject> loginUser(@Body RequestBody requestBody);

}
