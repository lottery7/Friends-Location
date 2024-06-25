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
import com.example.friendlocation.utils.ChatroomUtils;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class Chatroom extends AppCompatActivity {
    private ImageButton addFriendBtn;
    private EditText messageInput;
    private ImageButton sendMessageButton;
    private TextView title;
    private RecyclerView recyclerView;
    private String chatroomID;
    private ChatroomModel chatroomModel;
    private ChatroomRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular_chat);

        addFriendBtn = findViewById(R.id.chat_add_friend);
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageButton = findViewById(R.id.chat_send_message_button);
        title = findViewById(R.id.chat_title);
        recyclerView = findViewById(R.id.chat_recycler_view);

        sendMessageButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString();
            if (message.isEmpty()) {
                return;
            }
            sendMessage(message);
        });

        chatroomID = getIntent().getStringExtra("chatroomID");
        FirebaseUtils.getChatroomReference(chatroomID).get().addOnSuccessListener(snapshot -> {
            chatroomModel = snapshot.toObject(ChatroomModel.class);
            if (chatroomModel == null) {
                Log.v(this.getClass().toString(), chatroomID + " is not a chatroom ID");
            }
            if (chatroomModel.isGroup) {
                title.setText(chatroomModel.title);
            } else {
                ChatroomUtils.getOtherUserFromChat(chatroomModel).get().addOnSuccessListener(snapshot1 -> {
                    User otherUser = snapshot1.getValue(User.class);
                    if (otherUser == null)
                        return;

                    if (otherUser.name != null)
                        title.setText(otherUser.name);

                    addFriendBtn.setOnClickListener(view ->
                            FirebaseUtils.getCurrentUserDetails().child("usersWithKnownLocationUID/".concat(otherUser.id)).setValue(true)
                    );
                });
            }
            setupChatRecyclerView();
        });
    }

    private void setupChatRecyclerView() {
        Query query = FirebaseUtils
                .getChatroomMessagesReference(chatroomModel.id)
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
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - itemCount);
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

        FirebaseUtils.getChatroomMessagesReference(chatroomModel.id).add(chatMessage)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        chatroomModel.lastMessageText = message;
                        FirebaseUtils.getChatroomReference(chatroomModel.id).set(chatroomModel);
                        messageInput.setText("");
                    }
                });
    }
}