//package com.mlt.ets.rider.fragments;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.os.Looper;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.mlt.ets.rider.Helper.UrlManager;
//import com.mlt.ets.rider.R;
//import com.mlt.ets.rider.databinding.FragmentHomeBinding;
//import com.mlt.ets.rider.network.ApiService;
//import com.mlt.ets.rider.network.RetrofitClient;
//import com.mlt.ets.rider.utills.MapUtils;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//
//import okhttp3.MediaType;
//import okhttp3.RequestBody;
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class HomeFragment extends Fragment {
//
//    private UrlManager urlManager;
//    private FragmentHomeBinding binding;
//    private GoogleMap googleMap;
//    private CardView bookingCard;
//    private FusedLocationProviderClient fusedLocationClient;
//    private Marker destinationMarker;
//    private HomeViewModel homeViewModel;
//
//    private MapUtils mapUtils;
//
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Initialize urlManager and mapUtils
//        urlManager = new UrlManager(getContext());
//        mapUtils = new MapUtils(getContext());
//
//        // Get ViewModel
//        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
//
//        // Inflate the layout
//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        // Initialize FusedLocationProviderClient
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
//
//        // Set up Google Map
//        setupMap();
//
//        // Initialize booking button and card
//        Button btnBook = binding.btnBook;
//        bookingCard = binding.bookingCard; // Card for booking options
//        bookingCard.setVisibility(View.GONE); // Initially hide the card
//
//        // Show card UI on button click
//        btnBook.setOnClickListener(v -> bookingCard.setVisibility(View.VISIBLE));
//
//        // Set up radio group listener
//        RadioGroup bookingTypeRadioGroup = binding.bookingTypeRadioGroup;
//        EditText sourceEditText = binding.sourceEditText;
//        EditText destinationEditText = binding.destinationEditText;
//
//        bookingTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
//            if (checkedId == R.id.radioOther) {
//                // Show the EditText fields for "Others"
//                sourceEditText.setVisibility(View.VISIBLE);
//                destinationEditText.setVisibility(View.VISIBLE);
//            } else {
//                // Hide the EditText fields for other options
//                sourceEditText.setVisibility(View.GONE);
//                destinationEditText.setVisibility(View.GONE);
//            }
//        });
//
//        // Handle confirmation button click
//        Button btnConfirmBooking = binding.btnConfirmBooking;
//        btnConfirmBooking.setOnClickListener(v -> confirmBooking(root));
//
//        return root;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//
//    private void setupMap() {
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(new OnMapReadyCallback() {
//                @Override
//                public void onMapReady(GoogleMap map) {
//                    googleMap = map;
//                    getCurrentLocation();
//                    googleMap.setOnMapClickListener(HomeFragment.this::selectDestination);
//                }
//            });
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == 1000) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getCurrentLocation(); // Permission granted, fetch the location again
//            } else {
//                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    // Get current location
//    private void getCurrentLocation() {
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(10000);
//        locationRequest.setFastestInterval(5000);
//
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireActivity(), new String[]{
//                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
//            }, 1000);
//            return;
//        }
//
//        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) {
//                    return;
//                }
//                for (Location location : locationResult.getLocations()) {
//                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                    googleMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
//                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
//
//                    Log.d("HomeFragment", "Current Location: Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
//                    fusedLocationClient.removeLocationUpdates(this); // Stop updates after getting the location
//                }
//            }
//        }, Looper.getMainLooper());
//    }
//
//    // Handle destination selection on map click
//    private void selectDestination(LatLng latLng) {
//        // Update destination marker
//        if (destinationMarker != null) {
//            destinationMarker.remove();
//        }
//        destinationMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//
//        // Update ViewModel with selected destination
//        homeViewModel.setDestination(latLng);
//
//        // Log the selected destination's location
//        Log.d("HomeFragment", "Destination selected: Lat: " + latLng.latitude + ", Lng: " + latLng.longitude);
//
//        Toast.makeText(getContext(), "Destination selected", Toast.LENGTH_SHORT).show();
//    }
//
//    // Get current date in "Y-m-d" format
//    public String getCurrentDate() {
//        LocalDate currentDate = LocalDate.now();
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        return currentDate.format(dateFormatter);
//    }
//
//    // Get current time in "H:i" format
//    public String getCurrentTime() {
//        LocalTime currentTime = LocalTime.now();
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
//        return currentTime.format(timeFormatter);
//    }
//
//    // Handle booking confirmation
//    private void confirmBooking(View root) {
//        int selectedId = binding.bookingTypeRadioGroup.getCheckedRadioButtonId();
//        RadioButton selectedRadioButton = root.findViewById(selectedId);
//
//        if (selectedRadioButton != null) {
//            String bookingType = selectedRadioButton.getText().toString();
//            sendBookingToBackend(bookingType); // Call the method to send data to backend
//
//            bookingCard.setVisibility(View.GONE);
//        } else {
//            Toast.makeText(getContext(), "Please select a booking type", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // Send booking data to backend
//    // Send booking data to backend
//    private void sendBookingToBackend(String bookingType) {
//        if (urlManager == null) {
//            Log.e("HomeFragment", "urlManager is not initialized");
//            return;
//        }
//
//        String api_token = urlManager.getApiToken();
//        int user_id = urlManager.getUserId();
//
//
//        double EMLatitude = urlManager.getLatitude();
//        double EMLongitude = urlManager.getLongitude();
//
//        JSONObject bookingDetails = new JSONObject();
//        try {
//            bookingDetails.put("user_id", user_id);
//            bookingDetails.put("fcm_id", "sample_fcm_token_123");
//
//
//            // Add different JSON structure based on booking type
//            switch (bookingType) {
//                case "Home":
//                    bookingDetails.put("source_address", "123 Main St, City, State");
//                    bookingDetails.put("dest_address", "456 Another St, City, State");
//                    bookingDetails.put("source_lat", EMLatitude);
//                    bookingDetails.put("source_long", EMLongitude);
//                    break;
//
//                case "Work":
//                    bookingDetails.put("source_address", "789 Other St, City, State");
//                    bookingDetails.put("dest_address", "321 Different St, City, State");
//                    bookingDetails.put("source_lat", EMLatitude);
//                    bookingDetails.put("source_long", EMLongitude);
//                    break;
//
//                case "Others":
//                    // For "Others", assume source and destination are taken from EditTexts
//                    String customSource = binding.sourceEditText.getText().toString();
//                    String customDestination = binding.destinationEditText.getText().toString();
//
//                    if (customSource.isEmpty() || customDestination.isEmpty()) {
//                        Toast.makeText(getContext(), "Please provide both source and destination", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    bookingDetails.put("source_address", customSource);
//                    bookingDetails.put("dest_address", customDestination);
//                    bookingDetails.put("source_lat", EMLatitude);
//                    bookingDetails.put("source_long", EMLongitude);
//                    break;
//
//                default:
//                    Toast.makeText(getContext(), "Invalid booking type", Toast.LENGTH_SHORT).show();
//                    return;
//            }
//        } catch (JSONException e) {
//            Log.e("HomeFragment", "JSON exception: " + e.getMessage());
//        }
//
//        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), bookingDetails.toString());
//        Call<ResponseBody> call = apiService.bookNow(requestBody);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(getContext(), "Booking confirmed", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getContext(), "Booking failed: " + response.message(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(getContext(), "Booking failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//}
package com.mlt.ets.rider.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mlt.ets.rider.Helper.UrlManager;
import com.mlt.ets.rider.R;
import com.mlt.ets.rider.databinding.FragmentHomeBinding;
import com.mlt.ets.rider.network.ApiService;
import com.mlt.ets.rider.network.RetrofitClient;
import com.mlt.ets.rider.utills.MapUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private UrlManager urlManager;
    private FragmentHomeBinding binding;
    private GoogleMap googleMap;
    private CardView bookingCard;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker destinationMarker;
    private HomeViewModel homeViewModel;

    private MapUtils mapUtils;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize urlManager and mapUtils
        urlManager = new UrlManager(getContext());
        mapUtils = new MapUtils(getContext());

        // Get ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Inflate the layout
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Set up Google Map
        setupMap();

        // Initialize booking button and card
        Button btnBook = binding.btnBook;
        bookingCard = binding.bookingCard; // Card for booking options
        bookingCard.setVisibility(View.GONE); // Initially hide the card

        // Show card UI on button click
        btnBook.setOnClickListener(v -> bookingCard.setVisibility(View.VISIBLE));

        // Set up radio group listener
        RadioGroup bookingTypeRadioGroup = binding.bookingTypeRadioGroup;
        EditText sourceEditText = binding.sourceEditText;
        EditText destinationEditText = binding.destinationEditText;

        bookingTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioOther) {
                // Show the EditText fields for "Others"
                sourceEditText.setVisibility(View.VISIBLE);
                destinationEditText.setVisibility(View.VISIBLE);
            } else {
                // Hide the EditText fields for other options
                sourceEditText.setVisibility(View.GONE);
                destinationEditText.setVisibility(View.GONE);
            }
        });

        // Handle confirmation button click
        Button btnConfirmBooking = binding.btnConfirmBooking;
        btnConfirmBooking.setOnClickListener(v -> confirmBooking(root));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;
                    getCurrentLocation();
                    googleMap.setOnMapClickListener(HomeFragment.this::selectDestination);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation(); // Permission granted, fetch the location again
            } else {
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Get current location
    private void getCurrentLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1000);
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                    Log.d("HomeFragment", "Current Location: Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
                    fusedLocationClient.removeLocationUpdates(this); // Stop updates after getting the location
                }
            }
        }, Looper.getMainLooper());
    }

    // Handle destination selection on map click
    private void selectDestination(LatLng latLng) {
        // Update destination marker
        if (destinationMarker != null) {
            destinationMarker.remove();
        }
        destinationMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        // Update ViewModel with selected destination
        homeViewModel.setDestination(latLng);

        // Log the selected destination's location
        Log.d("HomeFragment", "Destination selected: Lat: " + latLng.latitude + ", Lng: " + latLng.longitude);

        Toast.makeText(getContext(), "Destination selected", Toast.LENGTH_SHORT).show();
    }


    // Handle booking confirmation
    private void confirmBooking(View root) {
        int selectedId = binding.bookingTypeRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = root.findViewById(selectedId);

        if (selectedRadioButton != null) {
            String bookingType = selectedRadioButton.getText().toString();
            sendBookingToBackend(bookingType); // Call the method to send data to backend

            bookingCard.setVisibility(View.GONE);
        } else {
            Toast.makeText(getContext(), "Please select a booking type", Toast.LENGTH_SHORT).show();
        }
    }

    // Send booking data to backend
    private void sendBookingToBackend(String bookingType) {
        if (urlManager == null) {
            Log.e("HomeFragment", "urlManager is not initialized");
            return;
        }

        String api_token = urlManager.getApiToken();
        int user_id = urlManager.getUserId();


        // Change the addresses based on booking type
        String customSource = binding.sourceEditText.getText().toString();
        String customDestination = binding.destinationEditText.getText().toString();

        JSONObject bookingDetails = new JSONObject();
        try {
            bookingDetails.put("user_id", user_id);
            bookingDetails.put("booking_type",bookingType);
            bookingDetails.put("api_token", api_token);
            bookingDetails.put("fcm_id", "sample_fcm_token_123");
            if(bookingType.equals("Others")){
                if (customSource.isEmpty() || customDestination.isEmpty()) {
                    Toast.makeText(getContext(), "Please provide both source and destination", Toast.LENGTH_SHORT).show();
                    return;
                }
                bookingDetails.put("source_address", customSource);
                bookingDetails.put("dest_address", customDestination);
            }


        } catch (JSONException e) {
            Log.e("HomeFragment", "JSON exception: " + e.getMessage());
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), bookingDetails.toString());
        Call<ResponseBody> call = apiService.bookNow( requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Booking confirmed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Booking failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Booking failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
