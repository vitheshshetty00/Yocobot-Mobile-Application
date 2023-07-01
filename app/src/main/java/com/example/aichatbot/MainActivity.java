package com.example.aichatbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.grpc.android.BuildConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "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";
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton,backBtn,clearBtn,copyBtn;
    List<Message> messageList;
    MessageAdapter messageAdapter;


    String userId;
    DatabaseReference chatsRef;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageList =  new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }else {
            Intent intent = new Intent(MainActivity.this, OnBoarding.class);
            startActivity(intent);
        }

        chatsRef = database.getReference("chats").child(userId);



        recyclerView = findViewById(R.id.recyclerView);
        welcomeTextView = findViewById(R.id.tvWelcome);
        messageEditText = findViewById(R.id.etMessage);
        sendButton = findViewById(R.id.btnSend);
        backBtn = findViewById(R.id.backBtn);



        clearBtn = findViewById(R.id.clearBtn);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        retrieveChat();

        clearBtn.setOnClickListener(v -> {

            chatsRef.removeValue();

            messageList.clear();
            messageAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageList.size());
        });



        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
        });

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

    void retrieveChat() {
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                welcomeTextView.setVisibility(TextView.GONE);
                messageList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                        Message message = messageSnapshot.getValue(Message.class);
                        messageList.add(message);
                    }
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(messageList.size() - 1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                welcomeTextView.setVisibility(TextView.VISIBLE);
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

                chatsRef.push().setValue(m);
            }
        });

    }

    void addResponse(String response) {
        messageList.remove(messageList.size()-1);
        addToChat(response, Message.SENT_BY_BOT);

    }



    void  callApi(String message) {

        Message m = new Message("Typing...", Message.SENT_BY_BOT);
        messageList.add(m);
        messageAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(messageList.size() - 1);

//        convert messegeList to string
        StringBuilder sb = new StringBuilder();
        for (Message m1 : messageList) {
            sb.append(m1.getMessage());
            sb.append("\n");
        }
        String chat = sb.toString();
        chat = chat.substring(0, chat.length() - 1);

//        addToChat("Typing...",Message.SENT_BY_BOT);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model","text-davinci-003");
            jsonBody.put("prompt",chat);
            jsonBody.put("max_tokens",1000);
            jsonBody.put("temperature",0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer " + API_KEY)
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