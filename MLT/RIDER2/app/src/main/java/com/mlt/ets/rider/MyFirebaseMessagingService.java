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
    import com.mlt.ets.rider.Helper.UrlManager;

    public class MyFirebaseMessagingService extends FirebaseMessagingService {

        private static final String CHANNEL_ID = "default_channel";
        private UrlManager urlManager;
        @Override
        public void onNewToken(@NonNull String token) {
            super.onNewToken(token);
            urlManager = new UrlManager(this);
            // Log or send the new token to your server
            Log.d("FCM", "New Token: " + token);
            urlManager.setFCMtoken(token);
        }
        @Override
        public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);

            // Initialize UrlManager
            urlManager = new UrlManager(this);

            // For testing purposes, use dummy data if no data is received from FCM
            String driverName = remoteMessage.getData().get("driver_name");
            String vehicleType = remoteMessage.getData().get("vehicle_type");
            String vehicleNumber = remoteMessage.getData().get("vehicle_number");
            String otp = remoteMessage.getData().get("otp");

            // If no data is received, use dummy values
            if (driverName == null) driverName = "John Doe";
            if (vehicleType == null) vehicleType = "Sedan";
            if (vehicleNumber == null) vehicleNumber = "AB 1234 XY";
            if (otp == null) otp = "123456";


            // Send a broadcast with driver details
            Intent broadcastIntent = new Intent("com.mlt.ets.rider.DRIVER_INFO");
            broadcastIntent.putExtra("driver_name", driverName);
            broadcastIntent.putExtra("vehicle_type", vehicleType);
            broadcastIntent.putExtra("vehicle_number", vehicleNumber);
            broadcastIntent.putExtra("otp", otp);
            sendBroadcast(broadcastIntent);

            // Show a notification for demonstration (optional)
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
//    here  displaying the details of or updation of the ui of the driverdetails in the cars  the details of the is getting saved in shared preference  but  the ui is not getting updated because the i am in  the same fragment na soo if i   go  to other fragment and come back and see then     then i am getting the  ui is beeing updated cant i update as the data gets saved