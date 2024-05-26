package com.example.friendlocation.Events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.R;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.User;
import com.example.friendlocation.adapters.UserAdapterSearchEvent;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

public class SearchUserEvent extends AppCompatActivity {
    private EditText searchInput;
    private ImageButton searchButton;
    private RecyclerView recyclerView;
    private UserAdapterSearchEvent adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_event);

        searchInput = findViewById(R.id.search_user_input);
        searchInput.requestFocus();
        searchButton = findViewById(R.id.search_user_button);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchButton.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString();
            if (searchTerm.isEmpty()) {
                searchInput.setError("Username is empty");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });
    }

    private void setupSearchRecyclerView(String searchTerm) {
        Query query = FirebaseUtils
                .getUsersCollection()
                .orderByChild("name")
                .startAt(searchTerm)
                .endAt(searchTerm + '\uf8ff');

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions
                .Builder<User>()
                .setQuery(query, snapshot -> {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        user.id = snapshot.getKey(); // Присваиваем uid из ключа snapshot'а
                    }
                    return user;
                })
                .build();

        adapter = new UserAdapterSearchEvent(options);
        adapter.setOnUserClickListener(new UserAdapterSearchEvent.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("name", user.name);
                resultIntent.putExtra("mail", user.email);
                resultIntent.putExtra("uid", user.id);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }
}