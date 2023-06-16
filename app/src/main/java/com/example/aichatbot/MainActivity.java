package com.example.aichatbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageList =  new ArrayList<>();



        recyclerView = findViewById(R.id.recyclerView);
        welcomeTextView = findViewById(R.id.tvWelcome);
        messageEditText = findViewById(R.id.etMessage);
        sendButton = findViewById(R.id.btnSend);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener(v -> {
            String query = messageEditText.getText().toString().trim();
            //Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
            if (!query.isEmpty()) {
                addToChat(query, Message.SENT_BY_ME);
                welcomeTextView.setVisibility(TextView.GONE);
                messageEditText.setText("");
                callApi(query);

            }
        });
    }

    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Message m = new Message(message, sentBy);
                messageList.add(m);
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageList.size() - 1);
            }
        });

    }

    void addResponse(String response) {
        messageList.remove(messageList.size()-1);
        addToChat(response, Message.SENT_BY_BOT);

    }

    void  callApi(String message) {

        addToChat("Typing...",Message.SENT_BY_BOT);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model","text-davinci-003");
            jsonBody.put("prompt",message);
            jsonBody.put("max_tokens",1000);
            jsonBody.put("temperature",0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization","Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String responseString = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        String text = jsonObject.getJSONArray("choices").getJSONObject(0).getString("text");
                        addResponse(text.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    addResponse("Error: " + response.body().toString());
                }
            }
        });

    }
}