package com.mlt.ets.rider.activity;
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
    private String selectedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);

        MaterialSpinner spGender = findViewById(R.id.spGender);
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

        if (name.isEmpty() || password.isEmpty() || email.isEmpty() || selectedGender.isEmpty()) {
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
                if (response.isSuccessful()) {
                    // Log the successful response
                    Log.d("SignUpResponse", "Response: " + response.body());
                    Toast.makeText(SignUpActivity.this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show();
                } else {
                    // Log the error response
                    Log.e("SignUpResponse", "Error: " + response.errorBody());
                    Toast.makeText(SignUpActivity.this, "Sign-Up Failed", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Log.e("SignUpError", t.getMessage());
                Toast.makeText(SignUpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
