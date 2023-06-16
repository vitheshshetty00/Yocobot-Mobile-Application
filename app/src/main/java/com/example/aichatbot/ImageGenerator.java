package com.example.aichatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class ImageGenerator extends AppCompatActivity {
    Button btnChat ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_generator);
        btnChat = findViewById(R.id.btnChat);

        btnChat.setOnClickListener(v -> {
            startActivity(new Intent(ImageGenerator.this, MainActivity.class));
        });
    }
}