package com.mlt.ets.rider.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.mlt.ets.rider.Device.DeviceTokenManager;
import com.mlt.ets.rider.Helper.UrlManager;
import com.mlt.ets.rider.MainActivity;
import com.mlt.ets.rider.R;
import com.mlt.ets.rider.network.ApiService;
import com.mlt.ets.rider.network.RetrofitClient;
import com.mlt.ets.rider.utills.MyEditText; // Make sure you have this custom EditText class

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    // Declare views
    private ImageView btnLogin;
    private View txtSignUp;
    private MyEditText etEmail, etPassword; // Add EditText for email and password

    private UrlManager urlManager;
    private FusedLocationProviderClient fusedLocationClient;
    private static final String PREFS_NAME = "LocationPrefs";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    private double currentLatitude, currentLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        urlManager = new UrlManager(this);
        // Initialize views
        btnLogin = findViewById(R.id.btnLogin);
        txtSignUp = findViewById(R.id.txtSignUp);
        etEmail = findViewById(R.id.etEmail); // Initialize email EditText
        etPassword = findViewById(R.id.etPassword); // Initialize password EditText

        String uniqueToken = DeviceTokenManager.getUniqueDeviceToken(this);
        Log.d("TOKEN OF DEVICE ", "Generated Unique Device Token: " + uniqueToken); // Log the token

        // Handle login button click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin(); // Call the method to handle login
            }
        });

        // Handle registration click
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Signup Activity
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
                        Log.d("LoginActivity", "Current Location: Latitude: " + currentLatitude + ", Longitude: " + currentLongitude);

                        // Optionally, store location in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putFloat(KEY_LATITUDE, (float) currentLatitude);
                        editor.putFloat(KEY_LONGITUDE, (float) currentLongitude);
                        editor.apply();
                    } else {
                        Log.w("LoginActivity", "Location is null");
                        Toast.makeText(LoginActivity.this, "Unable to retrieve location", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LoginActivity", "Failed to get location: " + e.getMessage());
                    Toast.makeText(LoginActivity.this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (email.isEmpty()) {
            etEmail.setError("Email cannot be empty");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password cannot be empty");
            etPassword.requestFocus();
            return;
        }

        // Create JSON object for the request
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Convert JSONObject to RequestBody
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        // Send data to backend using Retrofit
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.loginUser(requestBody); // Use the RequestBody here
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("LoginActivity Response", "Response Code: " + response.code());
                Log.d("LoginActivity Response", "Response Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Parse the response body as a JSON object
                        String responseBodyString = response.body().string();
                        Log.d("LoginActivity Raw Body", responseBodyString); // Log raw response for debugging
                        JSONObject jsonResponse = new JSONObject(responseBodyString);

                        // Process JSON response
                        int status = jsonResponse.getInt("success");
                        if (status == 1) {
                            JSONObject dataObject = jsonResponse.getJSONObject("data");
                            JSONObject userInfo = dataObject.getJSONObject("userinfo");
                            String apiToken = userInfo.getString("api_token");

                              int userID=userInfo.getInt("user_id");
                            urlManager.storeUserId(userID);
                            Log.d("LoginActivity Success", "Login successful, Token: " + apiToken);
                            Toast.makeText(LoginActivity.this, "Login Successful! Token: " + apiToken, Toast.LENGTH_SHORT).show();

                            // Store the API token in SharedPreferences
                            urlManager.storeApiToken(apiToken);
                            navigateToHome();
                        } else {
                            String errorMessage = jsonResponse.optString("message", "No message available");
                            Log.w("LoginActivity Failure", "Login failed: " + errorMessage);
                            Toast.makeText(LoginActivity.this, "Login Failed! " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("LoginActivity Error", "Error parsing response: " + e.getMessage());
                        Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.w("LoginActivity Error", "Response unsuccessful: " + response.message());
                    Log.d("LoginActivity Headers", response.headers().toString());
                    Toast.makeText(LoginActivity.this, "Login Failed! " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("LoginActivity", "Error: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {

        Log.d("NavigateToHome","entered the navigation method  ");
        Intent intent = new Intent(LoginActivity.this,MainActivity.class); // Replace ProfileActivity with your main activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the activity stack
        startActivity(intent);
        finish(); // Optionally finish the current activity
    }
}
