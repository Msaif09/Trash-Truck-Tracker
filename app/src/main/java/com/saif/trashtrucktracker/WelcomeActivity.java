package com.saif.trashtrucktracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    private Button setUserLoginButton;
    private Button setDriverLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        setUserLoginButton = findViewById(R.id.SetUserBtn);
        setDriverLoginBtn = findViewById(R.id.SetDriverBtn);

        setUserLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToUserActivity = new Intent(WelcomeActivity.this,UserLoginRagisterActivity.class);
                startActivity(goToUserActivity);
            }
        });

        setDriverLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToDriverActivity = new Intent(WelcomeActivity.this,DriverLoginRagisterActivity.class);
                startActivity(goToDriverActivity);
            }
        });

    }
}