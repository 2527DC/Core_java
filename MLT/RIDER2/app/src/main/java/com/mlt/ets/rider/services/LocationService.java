package com.mlt.ets.rider.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mlt.ets.rider.utills.MapUtils;

public class LocationService {
    private static final String TAG = "DriverLocationFetcher";
    private MapUtils mapUtils;
    private Context context;
    private  double latitude ;
    private  double longitude;

    // Constructor where you pass context to MapUtils
    public LocationService(Context context) {
        this.context = context;
        mapUtils = new MapUtils(context);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // Method to fetch the location based on user_id
    public void fetchDriverLocation(String driverId) {
        // Reference to the Firebase database, targeting the specific driver
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("locations/Drivers").child(driverId);

        // Add a listener to fetch the data of the specified driver
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get latitude and longitude values
                    Double latitude = snapshot.child("location/latitude").getValue(Double.class);
                    Double longitude = snapshot.child("location/longitude").getValue(Double.class);

                    if (latitude != null && longitude != null) {
                        // Log the latitude and longitude
                        Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);
                    } else {
                        Log.d(TAG, "Latitude or Longitude is missing.");
                    }
                } else {
                    Log.d(TAG, "Driver with ID " + driverId + " not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }

}
