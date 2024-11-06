package com.mlt.ets.rider.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleMapsRetrofitClient {

    private static Retrofit retrofit = null;

    public GoogleMapsRetrofitClient() {
        // Set up the logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Logs full request and response

        // Attach the interceptor to OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        // Initialize Retrofit for Google Maps API
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/") // Google Maps Base URL
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            new GoogleMapsRetrofitClient(); // Initialize the Google Maps client
        }
        return retrofit;
    }
}
