package com.mlt.ets.rider;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.mlt.ets.rider.Helper.UrlManager;
import com.mlt.ets.rider.activity.LoginActivity;
import com.mlt.ets.rider.databinding.ActivityMainBinding;
import com.mlt.ets.rider.services.LocationService;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private LocationService locationService;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private static final String TAG = "MainActivity";
    private ImageView imageView;
    private UrlManager urlManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize binding and set content view
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize UrlManager
        urlManager = new UrlManager(this);

        // Locate the ImageView in the navigation drawer header after the layout is set
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        imageView = headerView.findViewById(R.id.imageView);

        if (imageView != null) {
            String savedImageUriString = urlManager.getProfilePic();
            if (savedImageUriString != null) {
                Uri savedImageUri = Uri.parse(savedImageUriString);

                Glide.with(this).load(savedImageUri).into(imageView);
            } else {
                Log.e(TAG, "Profile picture URI not found");
            }
        } else {
            Log.e(TAG, "ImageView not found in navigation header");
        }

        // Initialize LocationService
        locationService = new LocationService(this);
        locationService.fetchDriverLocation("145");

        // Set up Toolbar
        setSupportActionBar(binding.appBarMain.toolbar);

        // Set up the AppBarConfiguration with top-level destinations
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_booking, R.id.nav_profile, R.id.nav_schedule, R.id.nav_review, R.id.nav_rac)
                .setOpenableLayout(drawer)
                .build();

        // Set up NavController to handle navigation
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Set user data in the navigation drawer header
        setUserDataInDrawerHeader(navigationView);
    }

    private void setUserDataInDrawerHeader(NavigationView navigationView) {
        // Get the header view of the NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Access the TextViews in the header layout
        TextView userNameTextView = headerView.findViewById(R.id.userNameTextView);
        TextView userEmailTextView = headerView.findViewById(R.id.userEmailTextView);

        // Retrieve the user data (using UrlManager methods)
        String userName = urlManager.getUserName();
        String userEmail = urlManager.getUserEmail();

        // Set the user data in the TextViews
        userNameTextView.setText(userName);
        userEmailTextView.setText(userEmail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem logoutItem = menu.findItem(R.id.action_logout);
        if (logoutItem != null) {
            setMenuItemStyle(logoutItem);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            handleLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleLogout() {
        UrlManager urlManager = new UrlManager(this);
        urlManager.clearAllData();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void setMenuItemStyle(MenuItem menuItem) {
        SpannableString styledText = new SpannableString(menuItem.getTitle());

        styledText.setSpan(new ForegroundColorSpan(Color.WHITE), 0, styledText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, styledText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new AbsoluteSizeSpan(18, true), 0, styledText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        menuItem.setTitle(styledText);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
    public void updateNavigationDrawerImage(Uri imageUri) {
        // Assuming you have a CircleImageView for profile picture in the navigation drawer

        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);  // Get the header view

        // Find the CircleImageView in the header view
        CircleImageView navHeaderImageView = headerView.findViewById(R.id.imageView); // Replace with the actual ID if necessary

        if (navHeaderImageView != null) {
            // Set the profile image using Glide (or setImageURI if you prefer)
            Glide.with(this)
                    .load(imageUri)
                    .into(navHeaderImageView);  // Use Glide to load the image
        } else {
            Log.e(TAG, "CircleImageView not found in navigation header");
        }
    }



    private void fetchDriverLocation(String driverId) {
        locationService.fetchDriverLocation(driverId);
    }
}
