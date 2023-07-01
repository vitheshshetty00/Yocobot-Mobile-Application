package com.example.aichatbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.aichatbot.databinding.ActivityImageGeneratorBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageGenerator extends AppCompatActivity {
    private static final String API_KEY = "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";
    Button btnChat ;
    EditText  etPrompt;
    ImageButton btnGenerateImage;

    ImageView placeholder ;
    LottieAnimationView animation_view;
    CardView cardView;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    String imageUrl ;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_generator);
        btnChat = findViewById(R.id.btnChat);
        btnGenerateImage = findViewById(R.id.btnGenerateImage);
        etPrompt = findViewById(R.id.etPromt);
        placeholder = findViewById(R.id.PlaceHolder);
        animation_view = findViewById(R.id.animation_view);
        cardView = findViewById(R.id.card);



        btnChat.setOnClickListener(v -> {
            startActivity(new Intent(ImageGenerator.this, MainActivity.class));
        });

        findViewById(R.id.backBtn).setOnClickListener(v -> {
            startActivity(new Intent(ImageGenerator.this, MenuActivity.class));
        });

        btnGenerateImage.setOnClickListener(v -> {
            String text = etPrompt.getText().toString().trim();

            callApi(text);
            etPrompt.setText("");
        });
    }

    private void callApi(String text) {
        progress(true);
        JSONObject object = new JSONObject();

        try {
            object.put("prompt",text);
            object.put("size","256x256");

        } catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .header("Authorization","Bearer "+API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(ImageGenerator.this,"Request failed",Toast.LENGTH_SHORT).show();

            }



            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                progress(false);
                if(response.isSuccessful()) {

                    String responseString = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        imageUrl = jsonObject.getJSONArray("data").getJSONObject(0).getString("url");
                        loadImage(imageUrl);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void loadImage(String imageUrl) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide
                        .with(ImageGenerator.this)
                        .load(imageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(placeholder);

            }
        });
    }

    private void progress(boolean isProgress){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isProgress){
                    animation_view.setVisibility(View.VISIBLE);
                    cardView.setVisibility(View.GONE);
                }
                else {
                    animation_view.setVisibility(View.GONE);
                    cardView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}