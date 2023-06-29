package com.example.aichatbot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;

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


public class VideoGenerator extends AppCompatActivity {
    Button btnChat ;
    EditText etPrompt;
    ImageButton btnGenerateVideo;

    VideoView placeholder ;
    LottieAnimationView animation_view;
    CardView cardView;
    String fetchUrl;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    String videoUrl ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_generator);

        btnChat = findViewById(R.id.btnChat);
        btnGenerateVideo = findViewById(R.id.btnGenerateVideo);
        etPrompt = findViewById(R.id.etPromt);
        placeholder = findViewById(R.id.PlaceHolder);
        animation_view = findViewById(R.id.animation_view);
        cardView = findViewById(R.id.card);

        btnChat.setOnClickListener(v -> {
            startActivity(new Intent(VideoGenerator.this, MainActivity.class));
        });

        findViewById(R.id.backBtn).setOnClickListener(v -> {
            startActivity(new Intent(VideoGenerator.this, MenuActivity.class));
        });

        btnGenerateVideo.setOnClickListener(v -> {
            String text = etPrompt.getText().toString().trim();

            callApi(text);
            etPrompt.setText("");
        });


    }

    private void callApi(String text) {
        progress(true);
        JSONObject object = new JSONObject();

        try {
            object.put("key","qtn4hZWa71ut9htkjHXOwGgjmPulkTrRfsO3749k9rcbdDWTV5pwAWOOCycP");
            object.put("prompt",text);
            object.put("scheduler","UniPCMultistepScheduler");
            object.put("negative_prompt","Low Quality");
            object.put("seconds",10);

        } catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://stablediffusionapi.com/api/v5/text2video")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(VideoGenerator.this,"Request failed",Toast.LENGTH_SHORT).show();

            }



            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if(response.isSuccessful()) {

                    String responseString = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        fetchUrl = jsonObject.getString("fetch_result");
                        int eta = jsonObject.getInt("eta");
                        fetchVideo(fetchUrl,eta);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void fetchVideo(String fetchUrl, int eta) {



// Schedule the API call after 20 seconds
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();

                // Schedule the API call after the specified ETA
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchVideoResult(fetchUrl);
                    }
                }, eta * 1000);
            }
        });
    }

    private void fetchVideoResult(String fetchUrl) {
        progress(true);
        JSONObject object = new JSONObject();

        try {
            object.put("key","qtn4hZWa71ut9htkjHXOwGgjmPulkTrRfsO3749k9rcbdDWTV5pwAWOOCycP");

        } catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url(fetchUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Toast.makeText(VideoGenerator.this,"Request failed",Toast.LENGTH_SHORT).show();

            }



            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                progress(false);
                if(response.isSuccessful()) {

                    String responseString = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);

                        if(jsonObject.getString("status").equals("processing")){
                            fetchVideo(fetchUrl,10);
                            return;
                        }

                        progress(false);

                        String videoUrl = jsonObject.getJSONArray("output").get(0).toString();
                        loadVideo(videoUrl);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    private void loadVideo(String videoUrl) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(VideoGenerator.this,videoUrl,Toast.LENGTH_SHORT).show();

                Uri uri = Uri.parse(videoUrl);
                int width = 350; // desired width in pixels
                int height = 350; // desired height in pixels

                ViewGroup.LayoutParams layoutParams = placeholder.getLayoutParams();
                layoutParams.width =width;
                layoutParams.height = height;
                placeholder.setLayoutParams(layoutParams);

                // sets the resource from the
                // videoUrl to the videoView
                placeholder.setVideoURI(uri);

                // creating object of
                // media controller class
                MediaController mediaController = new MediaController(VideoGenerator.this);

                // sets the anchor view
                // anchor view for the videoView
                mediaController.setAnchorView(placeholder);

                // sets the media player to the videoView
                mediaController.setMediaPlayer(placeholder);

                // sets the media controller to the videoView
                placeholder.setMediaController(mediaController);

                

                // Set the video path and start playing
                ;
                placeholder.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                    placeholder.start();
                });


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