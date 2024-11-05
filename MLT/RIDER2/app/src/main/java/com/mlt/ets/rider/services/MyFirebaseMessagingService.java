package com.mlt.ets.rider.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Log incoming messages
        Log.d("Myfirebasemessage", "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            Log.d("Myfirebasemessage", "Notification Message Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0) {
            Log.d("Myfirebasemessage", "Data Message: " + remoteMessage.getData());
        }
    }
}
