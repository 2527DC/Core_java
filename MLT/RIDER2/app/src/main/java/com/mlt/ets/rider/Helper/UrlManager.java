package com.mlt.ets.rider.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class UrlManager {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_API_TOKEN = "api_token";
    private static final String KEY_USER_ID = "user_id";
    private SharedPreferences sharedPreferences;

    public UrlManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Method to store API token
    public void storeApiToken(String apiToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_API_TOKEN, apiToken);
        editor.apply();
    }

    // Method to retrieve API token
    public String getApiToken() {
        return sharedPreferences.getString(KEY_API_TOKEN, null);
    }

    public void storeUserId(int userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    // Method to retrieve user ID
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1); // Return -1 if user ID is not found
    }
}
