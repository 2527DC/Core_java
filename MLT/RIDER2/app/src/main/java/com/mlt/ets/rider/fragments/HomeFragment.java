package com.mlt.ets.rider.fragments;

import static android.content.ContentValues.TAG;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.Status;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.mlt.ets.rider.Helper.URLS;
import com.mlt.ets.rider.Helper.UrlManager;
import com.mlt.ets.rider.R;
import com.mlt.ets.rider.databinding.FragmentHomeBinding;
import com.mlt.ets.rider.network.ApiService;
import com.mlt.ets.rider.network.RetrofitClient;
import com.mlt.ets.rider.utills.MapUtils;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment implements URLS {

    private UrlManager urlManager;
    private FragmentHomeBinding binding;
    private GoogleMap googleMap;
    private CardView bookingCard;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker destinationMarker;
    private HomeViewModel homeViewModel;
    private MapUtils mapUtils;
    private String sourceAddress = "";
    private String destinationAddress = "";

    private RadioGroup bookingTypeRadioGroup;
    private boolean locationUpdated = false;

    private AutocompleteSupportFragment sourceAutocomplete;
    private AutocompleteSupportFragment destinationAutocomplete;

    // Other variables...
    private LatLng sourceLatLng;
    private LatLng destinationLatLng;
    private Polyline currentPolyline; // For storing the polyline

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), Api_key);
        }
        // Initialize MapUtils

        urlManager = new UrlManager(getContext());
        mapUtils = new MapUtils(getContext());
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        setupMap();
        setupBookingButton(root);
        setupAutocompleteFragments();
        setupBookingTypeSelection();

        return root;
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

    private void setupBookingButton(View root) {
        bookingCard = binding.bookingCard;
        bookingCard.setVisibility(View.GONE);
        Button btnBook = binding.btnBook;

        btnBook.setOnClickListener(v -> bookingCard.setVisibility(View.VISIBLE));

        Button btnConfirmBooking = binding.btnConfirmBooking;
        btnConfirmBooking.setOnClickListener(v -> confirmBooking(root));
    }

    private void setupBookingTypeSelection() {
        bookingTypeRadioGroup = binding.bookingTypeRadioGroup;
        bookingTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioOther) {
                toggleAutocompleteVisibility(View.VISIBLE);
            } else {
                toggleAutocompleteVisibility(View.GONE);
                // Clear source and destination if the type is changed
                sourceAddress = "";
                destinationAddress = "";
                if (sourceAutocomplete != null) {
                    sourceAutocomplete.setText(""); // Clear source address field
                }
                if (destinationAutocomplete != null) {
                    destinationAutocomplete.setText(""); // Clear destination address field
                }
            }
        });
    }


    private void toggleAutocompleteVisibility(int visibility) {
        if (sourceAutocomplete != null && destinationAutocomplete != null) {
            sourceAutocomplete.getView().setVisibility(visibility);
            destinationAutocomplete.getView().setVisibility(visibility);
        }
    }

    private void setupAutocompleteFragments() {
        sourceAutocomplete = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment_source);
        destinationAutocomplete = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment_destination);

        if (sourceAutocomplete != null && destinationAutocomplete != null) {
            setupPlaceSelectionListeners();
        }
    }

    private void setupPlaceSelectionListeners() {
        sourceAutocomplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        destinationAutocomplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        sourceAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                sourceAddress = place.getName();
                sourceLatLng = place.getLatLng();
                LatLng sourceLatLng = place.getLatLng();
                if (sourceLatLng != null) {
                    addMarker(sourceLatLng, "Source: " + sourceAddress);
                    Log.d("HomeFragment", "Source Address: " + sourceAddress + " LatLng: " + sourceLatLng); // Log source address and LatLng
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                logPlaceSelectionError("source", status);
            }
        });

        destinationAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                destinationAddress = place.getName();
                destinationLatLng = place.getLatLng(); // Get destination LatLng
                LatLng destinationLatLng = place.getLatLng();
                if (destinationLatLng != null) {
                    addMarker(destinationLatLng, "Destination: " + destinationAddress);
                    Log.d("homeActivity", "Destination Address: " + destinationAddress + " LatLng: " + destinationLatLng); // Log destination address and LatLng
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                logPlaceSelectionError("destination", status);
            }
        });
    }

    private void addMarker(LatLng latLng, String title) {
        googleMap.addMarker(new MarkerOptions().position(latLng).title(title));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private void logPlaceSelectionError(String type, Status status) {
        Log.e(TAG, "Error selecting " + type + ": " + status.getStatusMessage());
        Toast.makeText(getContext(), "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
    }

    private void confirmBooking(View root) {
        int selectedId = binding.bookingTypeRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = root.findViewById(selectedId);

        if (selectedRadioButton != null) {
            String bookingType = selectedRadioButton.getText().toString();

            // Only check for latitude and longitude if the selected booking type is "Other"
            if ("Other".equals(bookingType)) {
                if (sourceLatLng == null || destinationLatLng == null) {
                    Toast.makeText(getContext(), "Please provide both source and destination locations", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Request directions from Google Maps API
                mapUtils.getDirections(googleMap, sourceLatLng, destinationLatLng);
            }

            // Log information for debugging
            Log.d("HomeFragment", "Booking Type: " + bookingType); // Log booking type
            Log.d("HomeFragment", "Source LatLng: " + sourceLatLng); // Log source LatLng
            Log.d("HomeFragment", "Destination LatLng: " + destinationLatLng); // Log destination LatLng

            // Call the method to send booking details to the backend
            sendBookingToBackend(bookingType);

            // Clear the selected radio button and the autocomplete fields
            binding.bookingTypeRadioGroup.clearCheck(); // Clear the radio button selection
            sourceAddress = ""; // Reset source address
            destinationAddress = ""; // Reset destination address

            // Clear the AutocompleteSupportFragment fields
            if (sourceAutocomplete != null) {
                sourceAutocomplete.setText(""); // Clear source address field
            }
            if (destinationAutocomplete != null) {
                destinationAutocomplete.setText(""); // Clear destination address field
            }

            bookingCard.setVisibility(View.GONE);

        } else {
            Toast.makeText(getContext(), "Please select a booking type", Toast.LENGTH_SHORT).show();
        }
    }



    private void sendBookingToBackend(String bookingType) {
        if (urlManager == null) {
            Log.e("HomeFragment", "urlManager is not initialized");
            return;
        }

        String api_token = urlManager.getApiToken();
        int user_id = urlManager.getUserId();

        // Change the addresses based on booking type
        JSONObject bookingDetails = new JSONObject();
        try {
            bookingDetails.put("user_id", user_id);
            bookingDetails.put("booking_type", bookingType);
            bookingDetails.put("api_token", api_token);
            bookingDetails.put("fcm_id", "sample_fcm_token_123");
            if (bookingType.equals("Other")) {
                if (sourceAddress.isEmpty() || destinationAddress.isEmpty()) {
                    Toast.makeText(getContext(), "Please provide both source and destination", Toast.LENGTH_SHORT).show();
                    return;
                }
                bookingDetails.put("source_address", sourceAddress);
                bookingDetails.put("dest_address", destinationAddress);
            }

            Log.d(TAG, "Booking Details: " + bookingDetails.toString()); // Log booking details

        } catch (JSONException e) {
            Log.e("HomeFragment", "JSON exception: " + e.getMessage());
            return; // Exit the method if JSON creation fails
        }

        // Check for internet connectivity before making the API call
        if (!isInternetAvailable()) {
            Toast.makeText(getContext(), "Internet is not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), bookingDetails.toString());

        // Making the network call in a try-catch block to handle UnknownHostException
        try {
            Call<ResponseBody> call = apiService.bookNow(requestBody);
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
                    // Check if the failure was due to a network error
                    if (t instanceof UnknownHostException) {
                        Toast.makeText(getContext(), "Internet is not connected", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Booking failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            // Catch any unexpected exceptions
            Toast.makeText(getContext(), "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Method to check if internet is available
    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1000);

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
                if (locationResult != null && !locationUpdated) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        addMarker(currentLatLng, "Your Location");
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        locationUpdated = true;
                    }
                }
            }
        }, Looper.getMainLooper());
    }

    private void selectDestination(LatLng latLng) {
        if (destinationMarker != null) {
            destinationMarker.remove();
        }
        destinationMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}
