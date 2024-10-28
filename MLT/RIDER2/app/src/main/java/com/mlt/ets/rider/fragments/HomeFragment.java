    package com.mlt.ets.rider.fragments;

    import static android.content.Context.MODE_PRIVATE;

    import android.Manifest;
    import android.content.Context;
    import android.content.SharedPreferences;
    import android.content.pm.PackageManager;

    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.RadioButton;
    import android.widget.Toast;
    import androidx.annotation.NonNull;
    import androidx.cardview.widget.CardView;
    import androidx.core.app.ActivityCompat;
    import androidx.fragment.app.Fragment;
    import androidx.lifecycle.ViewModelProvider;

    import com.google.android.gms.location.FusedLocationProviderClient;
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
        private static final String PREFS_NAME = "LocationPrefs";
        private static final String KEY_LATITUDE = "latitude";
        private static final String KEY_LONGITUDE = "longitude";

        private  double EMLatitude,EMLongitude;

        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {

            // Initialize urlManager
            urlManager = new UrlManager(getContext());
            // Get ViewModel
            homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

            // Inflate the layout
            binding = FragmentHomeBinding.inflate(inflater, container, false);
            View root = binding.getRoot();

            // Initialize FusedLocationProviderClient
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());



            // Set up Google Map
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        googleMap = map;
                        // Get and display the current location
                        getCurrentLocation();
                        googleMap.setOnMapClickListener(latLng -> selectDestination(latLng));
                    }
                });
            }

            // Initialize booking button and card
            Button btnBook = binding.btnBook;
            bookingCard = binding.bookingCard; // Card for booking options
            bookingCard.setVisibility(View.GONE); // Initially hide the card


            // Show card UI on button click
            btnBook.setOnClickListener(v -> {
                bookingCard.setVisibility(View.VISIBLE);
            });

            // Handle confirmation button click
            Button btnConfirmBooking = binding.btnConfirmBooking;
            btnConfirmBooking.setOnClickListener(v -> {
                int selectedId = binding.bookingTypeRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = root.findViewById(selectedId);

                if (selectedRadioButton != null) {
                    String bookingType = selectedRadioButton.getText().toString();
                    sendBookingToBackend(bookingType); // Call the method to send data to backend

                    bookingCard.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "Please select a booking type", Toast.LENGTH_SHORT).show();
                }
            });

            return root;
        }



        @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }

        // Check stored location and get current location if not available
        private void checkStoredLocation() {
            // Get SharedPreferences
            Context context = requireContext();
            double storedLat = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getFloat(KEY_LATITUDE, Float.NaN);
            double storedLng = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getFloat(KEY_LONGITUDE, Float.NaN);

            if (storedLat != Float.NaN && storedLng != Float.NaN) {
                // If location is already stored, use it
                LatLng currentLocation = new LatLng(storedLat, storedLng);
                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                Log.d("HomeFragment", "Current Location from storage: Lat: " + storedLat + ", Lng: " + storedLng);
            } else {
                // If no location stored, get current location
                getCurrentLocation();
            }
        }


        // Get current location
        private void getCurrentLocation() {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request permissions if not granted
                ActivityCompat.requestPermissions(requireActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                }, 1000);
                return;
            }

            // Get last known location
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                    // Store the current location in SharedPreferences
                    storeLocation(location.getLatitude(), location.getLongitude());

                    Log.d("HomeFragment", "Current Location: Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
                } else {
                    Toast.makeText(getContext(), "Unable to find current location", Toast.LENGTH_SHORT).show();
                }
            });
        }


        // Store the current location in SharedPreferences
        private void storeLocation(double latitude, double longitude) {

            EMLatitude =latitude;
            EMLongitude=longitude;

            Context context = requireContext();
            context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                    .putFloat(KEY_LATITUDE, (float) latitude)
                    .putFloat(KEY_LONGITUDE, (float) longitude)
                    .apply();
            Log.d("HomeFragment", "Location stored: Lat: " + latitude + ", Lng: " + longitude);
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
        // Method to get the current date in "Y-m-d" format
        public String getCurrentDate() {
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return currentDate.format(dateFormatter);
        }

        // Method to get the current time in "H:i" format
        public String getCurrentTime() {
            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            return currentTime.format(timeFormatter);
        }

            // Send booking data to backend
            private void sendBookingToBackend(String bookingType) {
                if (urlManager == null) {
                    Log.e("HomeFragment", "urlManager is not initialized");
                    return;
                }
                String api_token = urlManager.getApiToken();
                Log.d("HomeFragment"," the token fetched is " +"  :"+api_token);
             int user_id = urlManager.getUserId();

                String journey_date =getCurrentDate();
                String journey_time =getCurrentTime();



                // Change the addresses based on booking type
                String sourceAddress = bookingType.equals("Home") ? "123 Main St, City, State" : "789 Office Rd, City, State";
                String destAddress = bookingType.equals("Home") ? "456 Elm St, City, State" : "123 Corporate Ave, City, State";

                double sourceLat=bookingType.equals("Home") ?37.42091845633271:EMLatitude;
                double sourceLong = bookingType.equals("Home") ?-122.06804506480692:EMLongitude;

                double destLat = bookingType.equals("Home") ?EMLatitude:37.42091845633271;
                double destLong =bookingType.equals("Home") ?EMLongitude:-122.06804506480692;

                // Create the JSON object
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("user_id", user_id);
                    jsonBody.put("source_address", sourceAddress);
                    jsonBody.put("dest_address", destAddress);
                    jsonBody.put("journey_date", journey_date); // Future date in Y-m-d format
                    jsonBody.put("journey_time", journey_time); // Time in H:i format
                    jsonBody.put("fcm_id", "your_fcm_token"); // Optional, if used
                    jsonBody.put("source_lat", sourceLat); // Latitude of source
                    jsonBody.put("source_long", sourceLong); // Longitude of source
                    jsonBody.put("dest_lat", destLat); // Latitude of destination
                    jsonBody.put("dest_long", destLong); // Longitude of destination
                    jsonBody.put("api_token",api_token );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("HomeFragment", "Sending JSON data: " + jsonBody.toString());

            // Convert JSONObject to RequestBody
            RequestBody requestBody = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));

            // Send the request to the backend using your API interface
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            Call<ResponseBody> call = apiService.bookNow(requestBody);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            // Log and display the response data
                            String responseData = response.body() != null ? response.body().string() : "No Response Body";
                            Log.d("HomeFragment", "Response Data: " + responseData);
                            Toast.makeText(getContext(), "Booking confirmed: " + bookingType, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("HomeFragment", "Response Error: " + response.message());
                        Toast.makeText(getContext(), "Booking failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }






    }
