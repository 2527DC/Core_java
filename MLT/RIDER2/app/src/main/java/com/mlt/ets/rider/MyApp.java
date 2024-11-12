package com.mlt.ets.rider;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.firebase.FirebaseApp;

public class MyApp extends Application {

    SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
    String fcmToken = preferences.getString("FCM_Token", null); // Default value is null if the token is not found

}
