package com.mlt.ets.rider.services;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mlt.ets.rider.utills.MapUtils;

public class LocationService {

    private MapUtils mapUtils;

private Context context;
//    // Constructor where you pass context to MapUtils
    public LocationService(Context context) {
        // Initialize MapUtils with the given context
        this.context=context;
        mapUtils = new MapUtils(context);
    }

    // Method to fetch the location for a specific driver without using a model class
    public void fetchDriverLocation(String driverId) {
        // Firebase reference to fetch the location of a specific driver
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("locations/Driver/" + driverId + "/location");

        // Attach a listener to read the data at the specified location
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Directly access latitude and longitude from the snapshot
                if (dataSnapshot.exists()) {
                    double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    double longitude = dataSnapshot.child("longitude").getValue(Double.class);

                    LatLng driverLocation = new LatLng(latitude, longitude);  // Driver's current location (latitude, longitude)
                    LatLng pickupLocation = new LatLng(13.2005, 76.6394);  // Example pickup location (latitude, longitude)

                    // Call the calculateArrivalTime method to calculate the arrival time
                    mapUtils.calculateArrivalTime(context, driverLocation, pickupLocation);
                    Log.d("LocationService", context.toString());
                    // Log the fetched location data
                    Log.d("LocationService", "Driver " + driverId + " Location: Latitude = " + latitude + ", Longitude = " + longitude);

                    // You can use the fetched data (e.g., update UI or display on a map)
                } else {
                    Log.d("LocationService", "No location data found for Driver " + driverId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur while fetching data
                Log.e("LocationService", "Error fetching location: " + databaseError.getMessage());
            }
        });
    }
}
