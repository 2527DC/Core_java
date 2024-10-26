package com.mlt.ets.rider.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.hbb20.CountryCodePicker;
import com.mlt.ets.rider.R;
import com.mlt.ets.rider.network.ApiService;
import com.mlt.ets.rider.network.RetrofitClient;
import com.mlt.ets.rider.utills.MyEditText;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private MyEditText etName, etPassword, etEmail, phoneNumber;
    private MaterialSpinner spGender;
    private CountryCodePicker countryCodePicker;
    private String selectedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        phoneNumber = findViewById(R.id.phonenumber);
        countryCodePicker = findViewById(R.id.countryCodePicker);

        // Initialize spinner and set items
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
    }

    private void validateAndSignUp() {
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();

        if (name.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty() || selectedGender == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare JSON Object
        JSONObject signUpRequest = new JSONObject();
        try {
            signUpRequest.put("name", name);
            signUpRequest.put("password", password);
            signUpRequest.put("email", email);
            signUpRequest.put("gender", selectedGender);
            signUpRequest.put("countryCode", countryCode);
            signUpRequest.put("phone", phone);
        } catch (JSONException e) {
            Log.e("SignUpError", "JSON Error: " + e.getMessage());
            Toast.makeText(this, "Error creating JSON request", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create RequestBody
        RequestBody requestBody = RequestBody.create(signUpRequest.toString(), MediaType.get("application/json; charset=utf-8"));

        // Send data using Retrofit
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<JSONObject> call = apiService.signUpUser(requestBody); // Assuming signUpUser returns JSONObject

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject jsonResponse = response.body();
                        int success = jsonResponse.getInt("success");

                        if (success == 1) {
                            String message = jsonResponse.getString("message");
                            Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                            resetFields();
                            navigateToHome();
                        } else {
                            String message = jsonResponse.getString("message");
                            Toast.makeText(SignUpActivity.this, "Registration Failed: " + message, Toast.LENGTH_SHORT).show();
                            resetFields();
                        }
                    } catch (JSONException e) {
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
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.e("SignUpError", "Failure: " + t.getMessage());
                Toast.makeText(SignUpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                resetFields();
            }
        });
    }

    // Method to reset input fields and spinner
    private void resetFields() {
        etName.setText("");
        etPassword.setText("");
        etEmail.setText("");
        phoneNumber.setText("");
        spGender.setSelectedIndex(0);
        selectedGender = null;
        countryCodePicker.setDefaultCountryUsingNameCode("US"); // Reset country picker if needed
    }

    // Method to navigate to the home activity
    private void navigateToHome() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SignUpActivity", "onDestroy called");
    }
}
