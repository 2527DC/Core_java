package com.mlt.ets.rider;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mlt.ets.rider.Helper.UrlManager;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "default_channel";
    private static final String TAG = "FCM message ";
private UrlManager urlManager;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        urlManager= new UrlManager(this);
        urlManager.setFCMtoken(token);
        Log.d(TAG, "New token: " + token);


    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String message = null;
        String title = "New Notification"; // Default title if none provided

        // Check if the message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            message = remoteMessage.getNotification().getBody();
            title = remoteMessage.getNotification().getTitle();
            Log.d(TAG, "Message body: " + message);
        }

        // Show notification if there's a message to display
        if (message != null) {
            showNotification(title, message);
        }

        // Log any data payload
        if (remoteMessage.getData().size() > 0) {
            for (String key : remoteMessage.getData().keySet()) {
                String value = remoteMessage.getData().get(key);
                Log.d(TAG, "Key: " + key + ", Value: " + value);
            }
        }
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel description");
            notificationManager.createNotificationChannel(channel);
        }

        if (title != null && message != null) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(android.R.drawable.ic_notification_overlay) // Use a default icon for testing
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(getPendingIntent())
                    .build();

            Log.d(TAG, "Displaying notification");
            notificationManager.notify(0, notification);
        } else {
            Log.d(TAG, "Notification title or message is null");
        }
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
    }
}
