package com.mlt.ets.rider.utills;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mlt.ets.rider.Helper.UrlManager;
import com.mlt.ets.rider.network.ApiService;
import com.mlt.ets.rider.network.RetrofitClient;
import android.util.Log; // Import Log for logging

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapUtils {

    private UrlManager urlManager; // Declare UrlManager
    private ApiService apiService; // Declare ApiService

    // Constructor to initialize UrlManager and ApiService
    public MapUtils(Context context) {
        this.urlManager = new UrlManager(context); // Instantiate UrlManager here
        this.apiService = RetrofitClient.getClient().create(ApiService.class); // Initialize ApiService
    }

    public String getStoredAddressFromLatLong(Context context) {
        double latitude = urlManager.getLatitude();
        double longitude = urlManager.getLongitude(); // Retrieve longitude from UrlManager
        String employeeAddress = getAddressFromLatLong(context, latitude, longitude);
        urlManager.setEmployeAddress(employeeAddress);
        return employeeAddress;
    }

    public static String getAddressFromLatLong(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0); // Customize this to return more specific parts if needed
            } else {
                return "No address found";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Unable to get address";
        }
    }

    // Method to draw a route between source and destination on the map
    // Method to draw a route on the map using a list of LatLng points
    public void drawRoute(GoogleMap googleMap, List<LatLng> routePoints) {
        // Create a polyline from the route points
        Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                .addAll(routePoints) // Add all points in the routePoints list
                .width(5) // Set the width of the polyline
                .color(Color.BLUE) // Set the color of the polyline
                .geodesic(true)); // Make the line follow the curvature of the earth
    }

    // Method to get directions from source to destination using the API
    public void getDirections(final GoogleMap googleMap, LatLng source, LatLng destination) {
        String apiKey = "YOUR_API_KEY"; // Replace with your actual Google Maps API key

        String origin = source.latitude + "," + source.longitude;
        String dest = destination.latitude + "," + destination.longitude;

        // Make the API call to get directions
        apiService.getDirections(origin, dest, apiKey)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String jsonResponse = response.body().string();
                                Log.d("MapUtils", "Direction Response: " + jsonResponse);

                                // Parse the JSON response for route points
                                List<LatLng> routePoints = parseDirections(jsonResponse);
                                // Draw the route on the map using the routePoints
                                drawRoute(googleMap, routePoints);
                            } catch (IOException e) {
                                Log.e("MapUtils", "IOException while reading response: " + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                String errorResponse = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                Log.d("MapUtils", "Error response: " + errorResponse);
                            } catch (IOException e) {
                                Log.e("MapUtils", "Error reading error response: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("MapUtils", "Failure: " + t.getMessage());
                        t.printStackTrace();
                    }
                });
    }

    private List<LatLng> parseDirections(String json) {
        List<LatLng> path = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray routes = jsonObject.getJSONArray("routes");
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
                b = encoded.charAt(index++) - 63; // ASCII code adjustment
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result >> 1) ^ -(result & 1)); // Convert to latitude
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63; // ASCII code adjustment
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result >> 1) ^ -(result & 1)); // Convert to longitude
            lng += dlng;
            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }
        return poly;
    }
}
