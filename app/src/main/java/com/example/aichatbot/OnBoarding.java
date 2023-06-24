package com.example.aichatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class OnBoarding extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        //check if user is already logged in

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(OnBoarding.this,MenuActivity.class));
            finish();
        }

        findViewById(R.id.signInBtn).setOnClickListener(
                v -> {
                    startActivity(new Intent(OnBoarding.this,login_activity.class));
                }
        );
        findViewById(R.id.signUpBtn).setOnClickListener(
                v -> {
                    startActivity(new Intent(OnBoarding.this,RegisterActivity.class));
                }
        );
    }
}