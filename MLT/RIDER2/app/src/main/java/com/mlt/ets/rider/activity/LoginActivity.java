package com.mlt.ets.rider.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mlt.ets.rider.Device.DeviceTokenManager;
import com.mlt.ets.rider.Helper.UrlManager;
import com.mlt.ets.rider.MainActivity;
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

public class LoginActivity extends AppCompatActivity {

    private MapUtils mapUtils;
    private ImageView btnLogin;
    private View txtSignUp;
    private MyEditText etEmail, etPassword;
    private UrlManager urlManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mapUtils = new MapUtils(this);
        // Check if the user is already logged in
        SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            navigateToHome(); // Navigate to main activity if already logged in
            return; // Exit onCreate to prevent further execution
        }

        setContentView(R.layout.activity_login);

        urlManager = new UrlManager(this);

        // Initialize views
        btnLogin = findViewById(R.id.btnLogin);
        txtSignUp = findViewById(R.id.txtSignUp);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        String uniqueToken = DeviceTokenManager.getUniqueDeviceToken(this);
        Log.d("TOKEN OF DEVICE ", "Generated Unique Device Token: " + uniqueToken);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });



    }

    private void handleLogin() {
        String address = mapUtils.getStoredAddressFromLatLong(this);
        Log.d("chethan", address);
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        double emSourceLat = urlManager.getLatitude();
        double emSourceLong = urlManager.getLongitude();


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

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", email);
            jsonObject.put("EmSourceLat", emSourceLat);
            jsonObject.put("EmSourceLong", emSourceLong);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.loginUser(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBodyString = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBodyString);

                        int status = jsonResponse.getInt("success");
                        if (status == 1) {
                            JSONObject dataObject = jsonResponse.getJSONObject("data");
                            JSONObject userInfo = dataObject.getJSONObject("userinfo");
                            String apiToken = userInfo.getString("api_token");

                            int userID = userInfo.getInt("user_id");
                            urlManager.storeUserId(userID);
                            urlManager.storeApiToken(apiToken);

                            // Save login state in SharedPreferences
                            SharedPreferences.Editor editor = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

                            navigateToHome();


                        } else {
                            String errorMessage = jsonResponse.optString("message", "No message available");
                            Toast.makeText(LoginActivity.this, "Login Failed! " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed! " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }



}
