package com.mlt.ets.rider.network;

import com.mlt.ets.rider.Helper.URLS;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient  implements URLS {

    private static Retrofit retrofit = null;

    // Base URL of the API
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON conversion
                    .build();
        }
        return retrofit;
    }
}
