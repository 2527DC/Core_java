package com.mlt.ets.rider.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.mlt.ets.rider.R;
import com.mlt.ets.rider.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private GoogleMap googleMap;
    private CardView bookingCard;
    private FusedLocationProviderClient fusedLocationClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Get ViewModel
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

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

                Log.d("HomeFragment", "Current Location: Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
            } else {
                Toast.makeText(getContext(), "Unable to find current   location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Send booking data to backend
    private void sendBookingToBackend(String bookingType) {
        // TODO: Implement your API call to send booking data to backend
        // This is a placeholder toast message
        Toast.makeText(getContext(), "Booking confirmed: " + bookingType, Toast.LENGTH_SHORT).show();
    }
}
