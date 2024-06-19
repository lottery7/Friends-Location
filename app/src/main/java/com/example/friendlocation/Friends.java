package com.example.friendlocation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.adapters.FriendsRecyclerAdapter;
import com.example.friendlocation.adapters.SearchUserRecyclerAdapter;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import java.util.Objects;

public class Friends extends BottomBar {
    private ImageButton cancelSearch;
    private EditText searchUserInput;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CardView noFriendsCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        cancelSearch = findViewById(R.id.friends_cancel_search_button);
        searchUserInput = findViewById(R.id.friends_search);
        recyclerView = findViewById(R.id.friends_recycler_view);
        noFriendsCardView = findViewById(R.id.friends_nobody);

        searchUserInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    cancelSearch.setVisibility(View.GONE);
                    setupFriendsRecyclerView();
                } else {
                    cancelSearch.setVisibility(View.VISIBLE);
                    noFriendsCardView.setVisibility(View.GONE);
                    setupSearchRecyclerView(s.toString());
                }
            }
        });

        cancelSearch.setOnClickListener(v -> searchUserInput.setText(""));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        setupFriendsRecyclerView();
    }

    private void setupSearchRecyclerView(String searchTerm) {
        com.google.firebase.database.Query query = FirebaseUtils
                .getUsersCollection()
                .orderByChild("name")
                .startAt(searchTerm)
                .endAt(searchTerm + '\uf8ff');

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions
                .Builder<User>()
                .setQuery(query, User.class)
                .build();

        SearchUserRecyclerAdapter adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
    }

    private void setupFriendsRecyclerView() {
        Query query = FirebaseUtils
                .getCurrentUserDetails()
                .child("usersWithKnownLocationUID");

        FirebaseRecyclerOptions<String> options = new FirebaseRecyclerOptions
                .Builder<String>()
                .setQuery(query, new SnapshotParser<String>() {
                    @NonNull
                    @Override
                    public String parseSnapshot(@NonNull DataSnapshot dataSnapshot) {
                        return Objects.requireNonNull(dataSnapshot.getKey());
                    }
                })
                .build();

        FriendsRecyclerAdapter adapter = getFriendsRecyclerAdapter(options);
        recyclerView.setAdapter(adapter);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
    }

    @NonNull
    private FriendsRecyclerAdapter getFriendsRecyclerAdapter(FirebaseRecyclerOptions<String> options) {
        FriendsRecyclerAdapter adapter = new FriendsRecyclerAdapter(options, getApplicationContext());
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                if (adapter.getItemCount() == 0) {
                    noFriendsCardView.setVisibility(View.VISIBLE);
                } else {
                    noFriendsCardView.setVisibility(View.GONE);
                }
            }
        });
        adapter.startListening();
        return adapter;
    }
}
