package com.mlt.ets.rider.Device;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class DeviceTokenManager {

    private static final String PREFS_NAME = "AppPreferences";
    private static final String TOKEN_KEY = "unique_device_token";
    public static String getUniqueDeviceToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Check if token already exists
        String token = sharedPreferences.getString(TOKEN_KEY, null);

        // If token does not exist, generate a new one
        if (token == null) {
            token = UUID.randomUUID().toString(); // Generate a unique token
            // Save the token to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TOKEN_KEY, token);
            editor.apply(); // Save changes
        }

        return token; // Return the unique token
    }

}
