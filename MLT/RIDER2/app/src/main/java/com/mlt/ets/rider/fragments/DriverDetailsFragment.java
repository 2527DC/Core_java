package com.mlt.ets.rider.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.mlt.ets.rider.R;
import androidx.cardview.widget.CardView;

public class DriverDetailsFragment extends Fragment {

    private TextView noDriverMessage;

    private TextView driverName, vehicleType, vehicleNumber, otp;

    // State flag to track if a driver has been assigned
    private boolean isDriverAssigned = false;


    // Replace LinearLayout with CardView for driverDetailsLayout
    private CardView driverDetailsLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dariver_details, container, false);

        // Initialize views
        noDriverMessage = view.findViewById(R.id.no_driver_message);
        driverDetailsLayout = view.findViewById(R.id.driver_details_layout); // Now it’s CardView
        driverName = view.findViewById(R.id.driver_name);
        vehicleType = view.findViewById(R.id.vehicle_type);
        vehicleNumber = view.findViewById(R.id.vehicle_number);
        otp = view.findViewById(R.id.otp);

        // Set initial UI based on the state
        updateUI();

        // Register the broadcast receiver
        requireContext().registerReceiver(driverInfoReceiver, new IntentFilter("com.mlt.ets.rider.DRIVER_INFO"), Context.RECEIVER_NOT_EXPORTED);

        return view;
    }


    // Update the UI based on the isDriverAssigned state
    private void updateUI() {
        if (isDriverAssigned) {
            noDriverMessage.setVisibility(View.GONE);
            driverDetailsLayout.setVisibility(View.VISIBLE);
        } else {
            noDriverMessage.setVisibility(View.VISIBLE);
            driverDetailsLayout.setVisibility(View.GONE);
        }
    }

    // BroadcastReceiver to handle driver info
    private final BroadcastReceiver driverInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get driver information from the intent
            String driverNameValue = intent.getStringExtra("driver_name");
            String vehicleTypeValue = intent.getStringExtra("vehicle_type");
            String vehicleNumberValue = intent.getStringExtra("vehicle_number");
            String otpValue = intent.getStringExtra("otp");

            // Update state to indicate driver is assigned
            isDriverAssigned = true;

            // Update UI fields with received values
            driverName.setText(driverNameValue);
            vehicleType.setText(vehicleTypeValue);
            vehicleNumber.setText(vehicleNumberValue);
            otp.setText(otpValue);

            // Refresh the UI based on updated state
            updateUI();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireContext().unregisterReceiver(driverInfoReceiver);
    }
}
