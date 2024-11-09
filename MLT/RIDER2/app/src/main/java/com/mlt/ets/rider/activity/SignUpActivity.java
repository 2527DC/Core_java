package com.mlt.ets.rider.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.hbb20.CountryCodePicker;
import com.mlt.ets.rider.Helper.UrlManager;
import com.mlt.ets.rider.R;
import com.mlt.ets.rider.network.ApiService;
import com.mlt.ets.rider.network.RetrofitClient;
import com.mlt.ets.rider.utills.MapUtils;
import com.mlt.ets.rider.utills.MyEditText;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private int locationRetrievalCount = 0;
    private MyEditText etName, etPassword, etEmail, phoneNumber;
    private MaterialSpinner spGender;
    private CountryCodePicker countryCodePicker;
    private String selectedGender;
    private static final int LOCATION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationClient;
    private double currentLatitude, currentLongitude;
    private MapUtils mapUtils;
    private UrlManager urlManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mapUtils = new MapUtils(this);
        urlManager = new UrlManager(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        phoneNumber = findViewById(R.id.phonenumber);
        countryCodePicker = findViewById(R.id.countryCodePicker);

        spGender = findViewById(R.id.spGender);
        spGender.setItems("Select Gender", "Male", "Female");
        spGender.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                selectedGender = (position > 0) ? item : null;
            }
        });

        findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSignUp();
            }
        });

        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000); // Set interval as needed

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) return;

            // Get the current location
            currentLatitude = locationResult.getLastLocation().getLatitude();
            currentLongitude = locationResult.getLastLocation().getLongitude();

            Log.d("SignUpActivity", "Current Location: Latitude = " + currentLatitude + ", Longitude = " + currentLongitude);

            locationRetrievalCount++; // Increment the counter

            if (locationRetrievalCount == 6) {
                urlManager.storeLocation(currentLatitude, currentLongitude);

            }
        }
    };

    private void validateAndSignUp() {
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();

        String address = mapUtils.getStoredAddressFromLatLong(this);
        Log.d("chethan", address);

        Log.d("SignUpActivity", "Stored Location: Latitude = " + urlManager.getLatitude() + ", Longitude = " + urlManager.getLatitude());

        double EmSourceLat = currentLatitude;
        double EmSourceLong = currentLongitude;

        // Validate fields
        if (name.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty() || selectedGender == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Email validation (simple regex)
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Password validation (at least 6 characters)
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construct JSON for signup
        JSONObject signUpRequest = new JSONObject();
        try {
            signUpRequest.put("EmSourceLat", EmSourceLat);
            signUpRequest.put("address", address);
            signUpRequest.put("EmSourceLong", EmSourceLong);
            signUpRequest.put("user_name", name);
            signUpRequest.put("password", password);
            signUpRequest.put("emailid", email);
            signUpRequest.put("gender", selectedGender);
            signUpRequest.put("phone_code", countryCode);
            signUpRequest.put("mobno", phone);
        } catch (JSONException e) {
            Log.e("SignUpError", "JSON Error: " + e.getMessage());
            Toast.makeText(this, "Error creating JSON request", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBody = RequestBody.create(signUpRequest.toString(), MediaType.get("application/json; charset=utf-8"));
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.signUpUser(requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBodyString = response.body().string();
                        Log.d("SignUpActivity Response", responseBodyString);

                        JSONObject jsonResponse = new JSONObject(responseBodyString);
                        int success = jsonResponse.optInt("success", 0);

                        if (success == 1) {
                            String message = jsonResponse.optString("message", "Registration successful!");
                            Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                            resetFields();
                            navigateToHome();
                        } else {
                            String message = jsonResponse.optString("message", "Registration failed!");
                            Toast.makeText(SignUpActivity.this, "Registration Failed: " + message, Toast.LENGTH_SHORT).show();
                            resetFields();
                        }
                    } catch (Exception e) {
                        Log.e("SignUpError", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(SignUpActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("SignUpResponse", "Error: " + response.code() + " - " + response.message());
                    Toast.makeText(SignUpActivity.this, "Sign-Up Failed", Toast.LENGTH_SHORT).show();
                    resetFields();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("SignUpError", "Failure: " + t.getMessage());
                Toast.makeText(SignUpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                resetFields();
            }
        });
    }

    // Email validation function
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }

    private void resetFields() {
        etName.setText("");
        etPassword.setText("");
        etEmail.setText("");
        phoneNumber.setText("");
        spGender.setSelectedIndex(0);
        selectedGender = null;
        countryCodePicker.setDefaultCountryUsingNameCode("US");
    }

    private void navigateToHome() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Call the super method first
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback); // Stop location updates
        Log.d("SignUpActivity", "onDestroy called");
    }
}
