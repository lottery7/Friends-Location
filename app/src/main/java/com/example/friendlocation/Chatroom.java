package com.example.friendlocation;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.adapters.ChatroomRecyclerAdapter;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class Chatroom extends AppCompatActivity {
    private User otherUser;
    private EditText messageInput;
    private ImageButton sendMessageButton;
    private TextView otherUserName;
    private RecyclerView recyclerView;

    private String chatroomId;
    private ChatroomModel chatroomModel;

    private ChatroomRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular_chat);

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageButton = findViewById(R.id.chat_send_message_button);
        otherUserName = findViewById(R.id.chat_username);
        recyclerView = findViewById(R.id.chat_recycler_view);

        sendMessageButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString();
            if (message.isEmpty()) {
                return;
            }
            sendMessage(message);
        });

        String otherUserId = getIntent().getStringExtra("User id");
        if (otherUserId == null) {
            Log.v(this.getClass().toString(), "Other user id is not found in the intent");
            return;
        }

        FirebaseUtils.getUsersCollection()
            .child(otherUserId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    otherUser = task.getResult().getValue(User.class);
                    otherUserName.setText(otherUser.name);
                    chatroomId = FirebaseUtils.getChatroomId(FirebaseUtils.getCurrentUserID(), otherUser.id);
                    createChatroom();
                    setupChatRecyclerView();
                } else {
                    Log.v(this.getClass().toString(), "Other user id is not found in the database");
                }
            });
    }

    private void setupChatRecyclerView() {
        Query query = FirebaseUtils
                .getChatroomMessagesReference(chatroomId)
                .orderBy("date");

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions
                .Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();

        adapter = new ChatroomRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    private void sendMessage(String message) {
        chatroomModel.lastMessageDate = Timestamp.now();
        chatroomModel.lastMessageSenderId = FirebaseUtils.getCurrentUserID();

        ChatMessage chatMessage = new ChatMessage(
                message,
                FirebaseUtils.getCurrentUserID(),
                chatroomModel.lastMessageDate
        );

        FirebaseUtils.getChatroomMessagesReference(chatroomId).add(chatMessage)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        messageInput.setText("");
                        chatroomModel.lastMessageText = message;
                        FirebaseUtils.getChatroomReference(chatroomId).set(chatroomModel);
                    }
                });
    }

    private void createChatroom() {
        FirebaseUtils.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            chatroomModel = task.getResult().toObject(ChatroomModel.class);
            if (chatroomModel == null) {
                chatroomModel = new ChatroomModel(
                        chatroomId,
                        Arrays.asList(FirebaseUtils.getCurrentUserID(), otherUser.id),
                        null,
                        "",
                        ""
                );
                FirebaseUtils.getChatroomReference(chatroomId).set(chatroomModel);
            }
        });
    }
}