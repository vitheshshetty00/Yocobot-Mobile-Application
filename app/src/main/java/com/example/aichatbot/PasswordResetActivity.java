package com.example.aichatbot;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.aichatbot.databinding.ActivityPasswordResetBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    EditText mEmail;
    Button mResetBtn;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        findViewById(R.id.resetToLogin).setOnClickListener(
                v -> {
                    startActivity(new Intent(PasswordResetActivity.this,login_activity.class));
                }
        );
//        findViewById(R.id.resetToLogin).setOnClickListener(
//                v -> {
//                    startActivity(new Intent(PasswordResetActivity.this,login_activity.class));
//                }
//        );

        findViewById(R.id.ResetToRegisterBtn).setOnClickListener(
                v -> {
                    startActivity(new Intent(PasswordResetActivity.this,RegisterActivity.class));
                }
        );

        mEmail = findViewById(R.id.emailField);
        mResetBtn=findViewById(R.id.ResetBtn);

        fAuth = FirebaseAuth.getInstance();
        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  email= mEmail.getText().toString();
                fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(),"Check your mail box to reset password",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Error:"+e.toString(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


    }


}