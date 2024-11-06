package com.mlt.ets.rider.network;


import com.mlt.ets.rider.models.Booking;

import org.json.JSONObject;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // POST request for user sign-up
    @POST("api/user-registration")
    Call<ResponseBody> signUpUser(@Body RequestBody requestBody);

    @POST("/api/user-login")
    Call<ResponseBody> loginUser(@Body RequestBody requestBody);

    @POST("api/book-now")
    Call<ResponseBody> bookNow(@Body RequestBody requestBody);
    @POST("api/book-now")
    Call<ResponseBody> secduleBooking(@Body RequestBody requestBody);

    @POST("api/ride-history") // Replace with your actual endpoint
    Call<ResponseBody> getBookingHistory(@Body RequestBody requestBody);

    // New method to get directions
    @GET("maps/api/directions/json") // Google Maps Directions API endpoint
    Call<ResponseBody> getDirections(
            @Query("origin") String origin, // sourceLat,sourceLng
            @Query("destination") String destination, // destLat,destLng
            @Query("key") String apiKey // Your Google Maps API Key
    );
}

