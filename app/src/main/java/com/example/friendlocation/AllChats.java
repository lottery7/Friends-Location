package com.example.friendlocation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class AllChats extends AppCompatActivity {
    private ImageButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);

        searchButton = findViewById(R.id.all_chats_search_button);
        searchButton.setOnClickListener(v -> {
            startActivity(new Intent(AllChats.this, SearchUser.class));
        });
    }
}