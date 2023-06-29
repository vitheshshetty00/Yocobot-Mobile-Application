package com.example.aichatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViewById(R.id.btnSignOut).setOnClickListener(
                v -> {
                    //signing out
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MenuActivity.this, OnBoarding.class));
                    finish();

                }
        );

        findViewById(R.id.btnChatCard).setOnClickListener(
                v -> {
                    //signing out
                    startActivity(new Intent(MenuActivity.this, MainActivity.class));
                    finish();

                }
        );

        findViewById(R.id.btnImageCard).setOnClickListener(
                v -> {

                    startActivity(new Intent(MenuActivity.this, ImageGenerator.class));
                    finish();

                }
        );

        findViewById(R.id.btnVideoCard).setOnClickListener(
                v -> {

                    startActivity(new Intent(MenuActivity.this, VideoGenerator.class));
                    finish();

                }
        );
    }
}