package com.mlt.ets.rider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.mlt.ets.rider.activity.SignUpActivity;

public class LoginActivity extends AppCompatActivity {

    // Declare views
    private ImageView btnLogin;
    private View txtSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        btnLogin = findViewById(R.id.btnLogin);
        txtSignUp = findViewById(R.id.txtSignUp);

        // Handle login button click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle login action here (you can add your login logic)
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
}
