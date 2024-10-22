package com.mlt.ets.rider.network;

import com.mlt.ets.rider.viewModel.SignUpRequest;
import com.mlt.ets.rider.viewModel.SignUpResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // POST request for user sign-up
    @POST("register")
    Call<SignUpResponse> signUpUser(@Body SignUpRequest signUpRequest);
}
