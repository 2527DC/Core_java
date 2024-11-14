package com.mlt.ets.rider.fragments;

import static android.content.ContentValues.TAG;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.net.UnknownHostException;
import java.util.Arrays;

public class HomeFragment extends Fragment implements URLS {
    private UrlManager urlManager;
    private FragmentHomeBinding binding;
    private GoogleMap googleMap;
    private CardView bookingCard;
    private FusedLocationProviderClient fusedLocationClient;
    private HomeViewModel homeViewModel;
    private MapUtils mapUtils;
    private String sourceAddress = "";
    private String destinationAddress = "";
    private RadioGroup bookingTypeRadioGroup;
    private AutocompleteSupportFragment sourceAutocomplete;
    private AutocompleteSupportFragment destinationAutocomplete;
    private LatLng sourceLatLng;
    private LatLng destinationLatLng;
    private boolean isLocationVisible = false; // Toggle variable for location visibility

private   String driverName =" not yet assigned",
        vehicleType=" not yet assigned",
        vehicleNumber=" not yet assigned",
        otp=" not yet assigned";
    // BroadcastReceiver to listen for updates to driver details
    private final BroadcastReceiver driverInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract driver details from the received intent
            driverName = intent.getStringExtra("driver_name");
             vehicleType = intent.getStringExtra("vehicle_type");
         vehicleNumber = intent.getStringExtra("vehicle_number");
          otp = intent.getStringExtra("otp");

            // Store the received driver details into UrlManager or SharedPreferences
//            urlManager.StoreDrivedetails(driverName, vehicleType, vehicleNumber, otp);

            // Update the UI with the new driver details
            updateDriverDetails(driverName, vehicleType, vehicleNumber, otp);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout with ViewBinding first
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Google Places if not already initialized
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), Api_key);
        }

        // Initialize UrlManager, MapUtils, and ViewModel
        urlManager = new UrlManager(getContext());
        mapUtils = new MapUtils(getContext());
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Initialize Google Places if not already initialized
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), Api_key);
        }
        // Set initial driver details from UrlManager

        // Register the receiver to listen for the broadcast
        IntentFilter filter = new IntentFilter("com.mlt.ets.rider.DRIVER_INFO");
        getContext().registerReceiver(driverInfoReceiver, filter, Context.RECEIVER_NOT_EXPORTED);


        // Set up FusedLocationProviderClient for location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Floating Action Button for current location
        FloatingActionButton fabMyLocation = binding.fabMyLocation;
        fabMyLocation.setOnClickListener(v -> toggleLocationVisibility());

        // Floating Action Button for SOS
        FloatingActionButton fabSOS = binding.fabSOS;

        // Firebase Realtime Database reference for checking driver status
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("locations/Drivers")
                .child("161");

        // Add a listener to Firebase Database to observe status changes
        databaseReference.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer status = dataSnapshot.getValue(Integer.class);
                Log.d("HomeFragment", "The Status is: " + status);
                // Show or hide SOS button based on status
                if (status != null && status == 1) {
                    fabSOS.setVisibility(View.VISIBLE);
                } else {
                    fabSOS.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log errors if data fetching fails
                Log.e("Firebase", "Error fetching status: " + databaseError.getMessage());
            }
        });

        // Set click listener for the SOS button
        fabSOS.setOnClickListener(v -> handleSOSAction());

        // Click listener for the Driver Details button
        Button btnDriverDetails = binding.btnDriverDetails;
        btnDriverDetails.setOnClickListener(v -> toggleDriverDetailsVisibility());
        CardView cardDriverDetails = binding.driverDetailsLayout; // Ensure you have a CardView in XML with id 'driver_details_layout'

        // Initially hide the driver details card
        cardDriverDetails.setVisibility(View.GONE);

        // Track driver location button
        Button btnTDL = binding.btnTrackDriver;

        // Set click listener for the TDL button
        btnTDL.setOnClickListener(v -> handleTDLbtn());

        // Additional setup methods for map and UI components
        setupMap();
        setupBookingButton(root);
        setupAutocompleteFragments();
        setupBookingTypeSelection();

        return root;
    }

    // Method to update the UI with the current driver details
    private void updateDriverDetails(String  driverName, String vehicleType, String vehicleNumber, String otp) {
        // Set the text on the TextViews using the binding object
        binding.driverName.setText(driverName);
        binding.vehicleType.setText(vehicleType);
        binding.vehicleNumber.setText(vehicleNumber);
        binding.otp.setText(otp);
    }
    // Method to handle TDL button click action
    private void handleTDLbtn() {
        Toast.makeText(getContext(), "Tracking the driver location!", Toast.LENGTH_SHORT).show();
    }

    // Method to handle driverdetails  button click action
    private void toggleDriverDetailsVisibility() {
        // Get the current visibility of the driver details layout
        int currentVisibility = binding.driverDetailsLayout.getVisibility();

        // Toggle the visibility
        if (currentVisibility == View.VISIBLE) {
            // If it's visible, hide it
            binding.driverDetailsLayout.setVisibility(View.GONE);
        } else {
            // If it's not visible, show it
            binding.driverDetailsLayout.setVisibility(View.VISIBLE);
        }
    }

    // Method to handle SOS button click action
    private void handleSOSAction() {
        // Add your SOS action here, such as sending an alert or notifying emergency contacts
        Toast.makeText(getContext(), "SOS Activated!", Toast.LENGTH_SHORT).show();
    }


    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;

                }
            });
        }
    }
    private void toggleLocationVisibility() {
        if (isLocationVisible) {
            // Hide current location marker and reset the map camera position
            if (googleMap != null) {
                googleMap.clear(); // Removes all markers from the map
            }
            isLocationVisible = false;
        } else {
            // Check for location permission
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
            // Show current location on the map
            getCurrentLocation();
            isLocationVisible = true;
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
            if ("RAC".equals(bookingType)) {
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
            if (bookingType.equals("RAC")) {
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
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return false;
    }


    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        // Show the current location on the map
                        showLocationOnMap(location);
                    } else {
                        // If last location is null, request a new location update
                        LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationRequest.setInterval(5000); // Set the interval for location updates
                        locationRequest.setFastestInterval(2000);

                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get current location", e);
                    Toast.makeText(getContext(), "Failed to get current location", Toast.LENGTH_SHORT).show();
                });
    }

    // Callback for receiving location updates
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            // Get the most recent location update
            Location location = locationResult.getLastLocation();
            if (location != null) {
                showLocationOnMap(location);
                // Stop location updates after getting the location
                fusedLocationClient.removeLocationUpdates(locationCallback);
            }
        }
    };

    // Function to show location on the map
    private void showLocationOnMap(Location location) {
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
        googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister the receiver when the fragment is destroyed

    }
}
