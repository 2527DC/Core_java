package com.mlt.ets.rider;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mlt.ets.rider.Helper.UrlManager;
import com.mlt.ets.rider.activity.LoginActivity;
import com.mlt.ets.rider.databinding.ActivityMainBinding;
import com.mlt.ets.rider.services.LocationService;

public class MainActivity extends AppCompatActivity {
    private LocationService locationService;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize LocationService
        locationService = new LocationService(this);
        locationService.fetchDriverLocation("145");

        // Binding and setting content view
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setting up Toolbar
        setSupportActionBar(binding.appBarMain.toolbar);

        // Initialize DrawerLayout and NavigationView for the side menu
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Set up the AppBarConfiguration with top-level destinations
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_booking, R.id.nav_profile, R.id.nav_schedule, R.id.nav_review, R.id.nav_rac)
                .setOpenableLayout(drawer)
                .build();

        // NavController to handle navigation logic
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Set the user data in the navigation drawer header
        setUserDataInDrawerHeader(navigationView);

        // Get FCM token and log it
        getFCMToken();
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
//dykThQ4ZSOmOkoRW5cwI5k:APA91bH6WNMHQ4GNyqXDbRqSKRC_SR_s6e2mgSw-3np2dyjG3S_gXhY9fgPCpFKvXvycCtmbdOYhDTLizKCh5K6WxiRDbHGllb4aVqBlVIxibRMjxKjufYI
                    // Get the FCM token
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);  // Log the FCM token
                });
    }

    private void setUserDataInDrawerHeader(NavigationView navigationView) {
        // Get the header view of the NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Access the TextViews in the header layout
        TextView userNameTextView = headerView.findViewById(R.id.userNameTextView);
        TextView userEmailTextView = headerView.findViewById(R.id.userEmailTextView);

        // Get the instance of UrlManager
        UrlManager urlManager = new UrlManager(this);

        // Retrieve the user data (using UrlManager methods)
        String userName = urlManager.getUserName(); // You can keep this or replace it with the actual method for user name
        String userEmail = urlManager.getUserEmail();  // Fetch user email using UrlManager

        // Set the user data in the TextViews
        userNameTextView.setText(userName);
        userEmailTextView.setText(userEmail);  // Set the email fetched from UrlManager
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Optionally, modify the title style here (programmatically)
        MenuItem logoutItem = menu.findItem(R.id.action_logout);
        if (logoutItem != null) {
            // Set custom title styles programmatically
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
        // Clear session data
        UrlManager urlManager = new UrlManager(this);
        urlManager.clearAllData();

        // Redirect to Login Activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Show logout confirmation message
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    // Set the style of menu item, specifically for the Logout item
    private void setMenuItemStyle(MenuItem menuItem) {
        // Create a SpannableString to apply custom styles
        SpannableString styledText = new SpannableString(menuItem.getTitle());

        // Apply color, bold style, and size to the text
        styledText.setSpan(new ForegroundColorSpan(Color.WHITE), 0, styledText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, styledText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new AbsoluteSizeSpan(18, true), 0, styledText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Adjust size as needed

        // Apply the styled title to the menu item
        menuItem.setTitle(styledText);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // Fetch driver location for the given driver ID (in this case, ID 255)
    private void fechDriverLocation(String driverId) {
        locationService.fetchDriverLocation(driverId);
    }
}
