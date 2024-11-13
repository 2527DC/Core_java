package com.mlt.ets.rider;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "default_channel";
    private static final String TAG = "FCM message";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Example data for testing purposes
        String driverName = "John Doe";
        String vehicleType = "Sedan";
        String vehicleNumber = "XYZ 1234";
        String otp = "5678";

        // Send a broadcast with driver details
        Intent broadcastIntent = new Intent("com.mlt.ets.rider.DRIVER_INFO");
        broadcastIntent.putExtra("driver_name", driverName);
        broadcastIntent.putExtra("vehicle_type", vehicleType);
        broadcastIntent.putExtra("vehicle_number", vehicleNumber);
        broadcastIntent.putExtra("otp", otp);
        sendBroadcast(broadcastIntent);

        // Optional: Show a notification for demonstration
        showNotification("Driver Assigned", "Driver details received. Check the app.");
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        notificationManager.notify(0, notification);
    }
}
