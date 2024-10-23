package com.mlt.ets.rider.activity;

import android.content.Intent; // Import Intent
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mlt.ets.rider.R;
import com.mlt.ets.rider.network.ApiService;
import com.mlt.ets.rider.network.RetrofitClient;
import com.mlt.ets.rider.utills.MyEditText;
import com.mlt.ets.rider.viewModel.SignUpRequest;
import com.mlt.ets.rider.viewModel.SignUpResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private MyEditText etName, etPassword, etEmail;
    private MaterialSpinner spGender; // Declare the spinner here
    private String selectedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);

        spGender = findViewById(R.id.spGender); // Initialize the spinner here
        spGender.setItems("Select Gender", "Male", "Female"); // Add options to the spinner
        spGender.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                // Avoid assigning null value if "Select Gender" is chosen
                if (position > 0) {
                    selectedGender = item; // Set the selected gender only if it's not the first option
                } else {
                    selectedGender = null; // Reset selectedGender if "Select Gender" is chosen
                }
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

        if (name.isEmpty() || password.isEmpty() || email.isEmpty() || selectedGender == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data to send
        SignUpRequest signUpRequest = new SignUpRequest(name, password, email, selectedGender);

        // Send data using Retrofit
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<SignUpResponse> call = apiService.signUpUser(signUpRequest);

        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("SignUpResponse", "Response: " + response.body().getMessage());
                    Toast.makeText(SignUpActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    resetFields(); // Reset fields after successful sign-up
                    navigateToHome(); // Navigate to the home activity
                } else {
                    Log.e("SignUpResponse", "Error: " + response.code() + " - " + response.message());
                    Toast.makeText(SignUpActivity.this, "Sign-Up Failed", Toast.LENGTH_SHORT).show();
                    resetFields(); // Optionally reset fields after failure
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                // Log failure (network or server error)
                Log.e("SignUpError", "Failure: " + t.getMessage());
                Toast.makeText(SignUpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                resetFields(); // Reset fields on failure as well
            }
        });
    }

    // Method to reset input fields and spinner
    private void resetFields() {
        etName.setText("");
        etPassword.setText("");
        etEmail.setText("");
        spGender.setSelectedIndex(0); // Reset spinner to default option
        selectedGender = null; // Reset the selected gender
    }

    // Method to navigate to the home activity
    private void navigateToHome() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class); // Replace HomeActivity with your main activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the activity stack
        startActivity(intent);
        finish(); // Optionally finish the current activity
    }
}
