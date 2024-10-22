package com.mlt.ets.rider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.mlt.ets.rider.activity.SignUpActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find the register button by its ID
        findViewById(R.id.txtSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to start the SignupActivity
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}
