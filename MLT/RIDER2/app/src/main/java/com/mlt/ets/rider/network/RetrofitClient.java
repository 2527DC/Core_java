package com.mlt.ets.rider.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient{

    private static Retrofit retrofit = null;

    // Base URL of the API
        private static final String BASE_URL = "https://ets.mltcorporate.com/";

    // Constructor to initialize the logging interceptor and OkHttp client
    public RetrofitClient() {
        // Set up the logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Logs full request and response

        // Attach the interceptor to OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        // Initialize Retrofit with the OkHttpClient that includes the interceptor
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)  // Set the OkHttpClient with interceptor
                    .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON conversion
                    .build();
        }
    }

    // Static method to get Retrofit instance
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Create a new instance of RetrofitClient to initialize Retrofit
            new RetrofitClient();
        }
        return retrofit;
    }
}
