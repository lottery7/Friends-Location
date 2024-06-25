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

import com.example.friendlocation.adapters.AllChatsRecyclerAdapter;
import com.example.friendlocation.adapters.SearchUserRecyclerAdapter;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.Query;

public class AllChats extends BottomBar {
    private ImageButton cancelSearch;
    private EditText searchUserInput;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CardView noMessagesCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);

        cancelSearch = findViewById(R.id.all_chats_cancel_search_button);
        searchUserInput = findViewById(R.id.search_user_input);
        recyclerView = findViewById(R.id.all_chats_recycler_view);
        noMessagesCardView = findViewById(R.id.all_chats_no_messages);
        noMessagesCardView.setVisibility(View.GONE);

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
                    setupRecentChatsRecyclerView();
                } else {
                    cancelSearch.setVisibility(View.VISIBLE);
                    noMessagesCardView.setVisibility(View.GONE);
                    setupSearchRecyclerView(s.toString());
                }
            }
        });

        cancelSearch.setOnClickListener(v -> searchUserInput.setText(""));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        setupRecentChatsRecyclerView();

        //   /--- Bottom Bar ---/
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_bar);
        bottomNavigationView.setSelectedItemId(R.id.chats_case);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.events_case) {
                goToEvents();
                return true;
            } else if (itemId == R.id.chats_case) {
                // Stay in the current Chats activity
                return true;
            } else if (itemId == R.id.map_case) {
                goToMap();
                return true;
            } else if (itemId == R.id.friends_case) {
                goToFriends();
                return true;
            } else if (itemId == R.id.settings_case) {
                goToSetting();
                return true;
            } else {
                return false;
            }
        });
        //   /--- Bottom Bar ---/

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

    private void setupRecentChatsRecyclerView() {
        Query query = FirebaseUtils
                .getChatroomsCollection()
                .whereArrayContains("userIds", FirebaseUtils.getCurrentUserID())
                .orderBy("lastMessageDate")
                .whereNotEqualTo("lastMessageDate", null);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions
                .Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class)
                .build();

        AllChatsRecyclerAdapter adapter = getAllChatsRecyclerAdapter(options);
        recyclerView.setAdapter(adapter);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
    }

    @NonNull
    private AllChatsRecyclerAdapter getAllChatsRecyclerAdapter(FirestoreRecyclerOptions<ChatroomModel> options) {
        AllChatsRecyclerAdapter adapter = new AllChatsRecyclerAdapter(options, getApplicationContext());
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
            }

            void checkEmpty() {
                noMessagesCardView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        });
        adapter.startListening();
        return adapter;
    }
}
