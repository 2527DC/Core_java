package com.mlt.ets.rider.utills;

import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mlt.ets.rider.Helper.URLS;
import com.mlt.ets.rider.Helper.UrlManager;
import com.mlt.ets.rider.network.DirectionsApiService;
import com.mlt.ets.rider.network.GoogleMapsRetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapUtils implements URLS {

    private UrlManager urlManager;
    private DirectionsApiService directionsApiService;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference driverLocationRef;

    public MapUtils(Context context) {
        this.urlManager = new UrlManager(context);
        this.directionsApiService = GoogleMapsRetrofitClient.getClient().create(DirectionsApiService.class);
        this.firebaseDatabase = FirebaseDatabase.getInstance();  // Initialize Firebase
        this.driverLocationRef = firebaseDatabase.getReference("driverLocation"); // Assuming Firebase path for driver location
    }

    public String getStoredAddressFromLatLong(Context context) {
        double latitude = urlManager.getLatitude();
        double longitude = urlManager.getLongitude();
        String employeeAddress = getAddressFromLatLong(context, latitude, longitude);
    //        urlManager.setEmployeAddress(employeeAddress);
        return employeeAddress;
    }

    public static String getAddressFromLatLong(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            } else {
                return "No address found";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Unable to get address";
        }
    }

    public void drawRoute(GoogleMap googleMap, List<LatLng> routePoints) {
        Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                .addAll(routePoints)
                .width(20)
                .color(Color.BLUE)
                .geodesic(true));
    }

    public void getDirections(final GoogleMap googleMap, LatLng source, LatLng destination) {
        String apiKey = Api_key;

        String origin = source.latitude + "," + source.longitude;
        String dest = destination.latitude + "," + destination.longitude;

        directionsApiService.getDirections(origin, dest, apiKey)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String responseString = response.body().string();
                                JSONObject jsonResponse = new JSONObject(responseString);
                                List<LatLng> routePoints = parseDirections(jsonResponse);
                                drawRoute(googleMap, routePoints);
                            } catch (Exception e) {
                                Log.e("MapUtils", "Error processing response: " + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("MapUtils", "Error response: " + response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("MapUtils", "Failure: " + t.getMessage());
                        t.printStackTrace();
                    }
                });
    }

    private List<LatLng> parseDirections(JSONObject jsonResponse) {
        List<LatLng> path = new ArrayList<>();
        try {
            JSONArray routes = jsonResponse.getJSONArray("routes");
            if (routes.length() > 0) {
                JSONObject route = routes.getJSONObject(0);
                JSONArray legs = route.getJSONArray("legs");
                if (legs.length() > 0) {
                    JSONObject leg = legs.getJSONObject(0);
                    JSONArray steps = leg.getJSONArray("steps");
                    for (int i = 0; i < steps.length(); i++) {
                        JSONObject step = steps.getJSONObject(i);
                        String polyline = step.getJSONObject("polyline").getString("points");
                        path.addAll(decodePolyline(polyline));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return path;
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result >> 1) ^ -(result & 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result >> 1) ^ -(result & 1));
            lng += dlng;
            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }
        return poly;
    }

    public void calculateArrivalTime(final Context context, LatLng driverLocation, LatLng pickupLocation) {
        String apiKey = Api_key;

        String origin = driverLocation.latitude + "," + driverLocation.longitude;
        String destination = pickupLocation.latitude + "," + pickupLocation.longitude;

        directionsApiService.getDirections(origin, destination, apiKey)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String responseString = response.body().string();
                                JSONObject jsonResponse = new JSONObject(responseString);
                                int durationInSeconds = parseDuration(jsonResponse);

                                if (durationInSeconds != -1) {
                                    long currentTimeInMillis = System.currentTimeMillis();
                                    long arrivalTimeInMillis = currentTimeInMillis + (durationInSeconds * 1000);
                                    String arrivalTime = convertMillisToTime(arrivalTimeInMillis);
                                    Log.d("MapUtils", "Estimated Arrival Time: " + arrivalTime);
                                } else {
                                    Log.e("MapUtils", "Could not extract duration from API response.");
                                }
                            } catch (Exception e) {
                                Log.e("MapUtils", "Error processing response: " + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("MapUtils", "Error response: " + response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("MapUtils", "Failure: " + t.getMessage());
                        t.printStackTrace();
                    }
                });
    }

    private int parseDuration(JSONObject jsonResponse) {
        try {
            JSONArray routes = jsonResponse.getJSONArray("routes");
            if (routes.length() > 0) {
                JSONObject route = routes.getJSONObject(0);
                JSONArray legs = route.getJSONArray("legs");
                if (legs.length() > 0) {
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject duration = leg.getJSONObject("duration");
                    return duration.getInt("value");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private String convertMillisToTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    // Method to listen for updates in the driver's location from Firebase
    public void listenForDriverLocationUpdates(final LatLng pickupLocation, final Context context) {
        driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double driverLat = dataSnapshot.child("latitude").getValue(Double.class);
                    double driverLng = dataSnapshot.child("longitude").getValue(Double.class);
                    LatLng driverLocation = new LatLng(driverLat, driverLng);

                    // Call calculateArrivalTime whenever the driver's location updates
                    calculateArrivalTime(context, driverLocation, pickupLocation);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MapUtils", "Error: " + databaseError.getMessage());
            }
        });
    }
}
