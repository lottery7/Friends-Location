package com.example.friendlocation;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.adapters.SearchUserRecyclerAdapter;
import com.example.friendlocation.util.FirebaseUtil;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

public class SearchUser extends AppCompatActivity {
    private EditText searchInput;
    private ImageButton searchButton;
    private RecyclerView recyclerView;
    private SearchUserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

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
        Query query = FirebaseUtil
                .getUsersCollection()
                .orderByChild("name")
                .startAt(searchTerm)
                .endAt(searchTerm + '\uf8ff');

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions
                .Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}