package com.mlt.ets.rider.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mlt.ets.rider.Helper.UrlManager;

public class LocationService extends Service {
    private UrlManager urlManager;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference databaseReference;
    private Handler handler;
    private Runnable runnable;

    private static final String TAG = "LocationService";

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        // Initialize UrlManager
        urlManager = new UrlManager(this);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                getLocationAndSendToFirebase();
                handler.postDelayed(this, 3000); // 3 seconds
            }
        };
        handler.post(runnable);
    }

    private void getLocationAndSendToFirebase() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permissions not granted");
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        // Get current location
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.d("Location service ", "Location: " + latitude + ", " + longitude);

                        // Update location to Firebase
                        String userId =Integer.toString(urlManager.getUserId()) ; // Replace with actual user ID
                        databaseReference.child(userId).child("location").setValue(location)
                                .addOnSuccessListener(aVoid -> Log.d("Location service ", "Location updated in Firebase"))
                                .addOnFailureListener(e -> Log.e("Location service ", "Failed to update location: " + e.getMessage()));
                    }
                });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Stop the handler when the service is destroyed
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
