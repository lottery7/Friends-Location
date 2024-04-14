package com.example.friendlocation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.adapters.AllChatsRecyclerAdapter;
import com.example.friendlocation.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class AllChats extends AppCompatActivity {
    private ImageButton searchButton;
    private RecyclerView recyclerView;
    private CardView noMessagesCardView;
    private AllChatsRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);

        searchButton = findViewById(R.id.all_chats_search_button);
        recyclerView = findViewById(R.id.all_chats_recycler_view);
        noMessagesCardView = findViewById(R.id.all_chats_no_messages);

        searchButton.setOnClickListener(v -> {
            startActivity(new Intent(AllChats.this, SearchUser.class));
        });
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        Query query = FirebaseUtil
                .getChatroomsCollection()
                .whereArrayContains("userIds", FirebaseUtil.getCurrentUserID())
                .orderBy("lastMessageDate")
                .whereNotEqualTo("lastMessageDate", null);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions
                .Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class)
                .build();

        adapter = new AllChatsRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setReverseLayout(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                noMessagesCardView.setVisibility(View.GONE);
            }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if (itemCount == 1) {
                    noMessagesCardView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}