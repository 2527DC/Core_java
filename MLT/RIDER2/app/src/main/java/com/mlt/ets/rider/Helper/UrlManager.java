package com.mlt.ets.rider.Helper;

import android.content.Context;
import android.content.SharedPreferences;



public class UrlManager {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_API_TOKEN = "api_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private SharedPreferences sharedPreferences;
    private String  EmployeAddress;
    private String OfficeAddress;

    public String getEmployeAddress() {
        return EmployeAddress;
    }

    public void setEmployeAddress(String employeAddress) {
        EmployeAddress = employeAddress;
    }

    public String getOfficeAddress() {
        return OfficeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        OfficeAddress = officeAddress;
    }

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


    // Method to store latitude and longitude
    public void storeLocation(double latitude, double longitude) {
        if (!isLocationStored()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(KEY_LATITUDE, (float) latitude);
            editor.putFloat(KEY_LONGITUDE, (float) longitude);
            editor.apply();
        }
    }
    public boolean isLocationStored() {
        return sharedPreferences.contains(KEY_LATITUDE) && sharedPreferences.contains(KEY_LONGITUDE);
    }
    // Method to retrieve latitude
    public double getLatitude() {
        return sharedPreferences.getFloat(KEY_LATITUDE, 0.0f);
    }

    // Method to retrieve longitude
    public double getLongitude() {
        return sharedPreferences.getFloat(KEY_LONGITUDE, 0.0f);
    }
    public void clearAllData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all data stored in SharedPreferences
        editor.apply();
    }

    public UrlManager() {
    }
}
