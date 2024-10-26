package com.mlt.ets.rider.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.mlt.ets.rider.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    // Declare your views
    private ImageView backArrow;
    private CircleImageView imgProfile;
    private TextView txtUserName, txtEditUser, txtReview, txtPassword, txtPassword2;
    private Switch switchNotifications;
    private Button btnLogout;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);  // Link to your layout

        // Initialize your views
        backArrow = findViewById(R.id.backArrow);
        imgProfile = findViewById(R.id.img_profile);
        txtUserName = findViewById(R.id.txtuserName);
        txtEditUser = findViewById(R.id.txtEdituser);
        txtReview = findViewById(R.id.txtReview);
        txtPassword = findViewById(R.id.txtPassword);
        txtPassword2 = findViewById(R.id.txtPassword2);
        switchNotifications = findViewById(R.id.my_switch);
        btnLogout = findViewById(R.id.btnLogout);


        // Set click listener for the back arrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back or close the activity
                finish();
            }
        });

        // Set click listener for Edit User
        txtEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement the logic to edit user details
                // Example: Start a new activity for editing the profile
            }
        });

        // Set click listener for Review TextView
        txtReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the Review section
            }
        });

        // Set click listener for Password TextView
        txtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the Change Password section
            }
        });

        // Set click listener for Password2 TextView
        txtPassword2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Bookings action
            }
        });

        // Set listener for Notifications switch
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Implement logic for enabling/disabling notifications
        });

        // Set click listener for Logout button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement logout logic
                // Example: Clear session and navigate to login screen
            }
        });
    }
}
