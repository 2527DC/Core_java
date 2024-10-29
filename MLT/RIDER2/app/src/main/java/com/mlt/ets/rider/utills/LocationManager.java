package com.mlt.ets.rider.utills;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.mlt.ets.rider.Helper.UrlManager;

public class LocationManager {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private UrlManager urlManager;
    private double currentLatitude, currentLongitude;

    public LocationManager(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        urlManager = new UrlManager(context); // Initialize UrlManager
    }

    public void startLocationUpdates(Context context, LocationUpdateCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000); // 5 seconds interval
        locationRequest.setFastestInterval(2000); // 2 seconds fastest interval

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    Log.w("LocationManager", "Location update is null");
                    callback.onLocationError("Unable to retrieve location updates");
                    return;
                }

                currentLatitude = locationResult.getLastLocation().getLatitude();
                currentLongitude = locationResult.getLastLocation().getLongitude();

                Log.d("LocationManager", "Updated Location: Latitude: " + currentLatitude + ", Longitude: " + currentLongitude);

                // Check if the first location is already stored, and store it if not
                if (!urlManager.isLocationStored()) {
                    urlManager.storeLocation(currentLatitude, currentLongitude);
                    Log.d("LocationManager", "First location stored: Latitude: " + currentLatitude + ", Longitude: " + currentLongitude);
                }

                callback.onLocationUpdated(currentLatitude, currentLongitude);
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            Log.d("LocationManager", "Location updates stopped.");
        }
    }

    public interface LocationUpdateCallback {
        void onLocationUpdated(double latitude, double longitude);
        void onLocationError(String error);
    }
}
