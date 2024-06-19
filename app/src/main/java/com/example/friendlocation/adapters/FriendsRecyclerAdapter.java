package com.example.friendlocation.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.Chatroom;
import com.example.friendlocation.ChatroomModel;
import com.example.friendlocation.R;
import com.example.friendlocation.utils.User;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.ChatroomUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class FriendsRecyclerAdapter extends FirebaseRecyclerAdapter<String, FriendsRecyclerAdapter.UIDViewHolder> {
    private final Context context;

    public FriendsRecyclerAdapter(@NonNull FirebaseRecyclerOptions<String> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UIDViewHolder holder, int position, @NonNull String uid) {
        FirebaseUtils.getUserDetails(uid).get().addOnSuccessListener(dataSnapshot -> {
            User user = dataSnapshot.getValue(User.class);
            if (user == null) {
                return;
            }

            String username = user.name;
            holder.usernameTextView.setText(username);
            holder.emailTextView.setText(user.email);
            holder.itemView.setOnClickListener(view -> {
                goToChatWith(user);
            });
            holder.removeFromFriends.setOnClickListener(view -> {
                FirebaseUtils.getCurrentUserDetails().child("usersWithKnownLocationUID/".concat(uid)).removeValue();
            });
        });
    }

    private void goToChatWith(User user) {
        String currentUserId = FirebaseUtils.getCurrentUserID();
        FirebaseUtils
                .getChatroomReference(ChatroomUtils.getChatId(currentUserId, user.id))
                .get()
                .addOnCompleteListener(snapshot -> {
                    ChatroomModel chatroomModel = snapshot.getResult().toObject(ChatroomModel.class);
                    if (chatroomModel != null) {
                        chatroomID = chatroomModel.id;
                        startChatroomIntent();
                        return;
                    }
                    chatroomModel = ChatroomUtils.createChatModel(currentUserId, user.id);
                    chatroomID = chatroomModel.id;
                    FirebaseUtils.getChatroomReference(chatroomID).set(chatroomModel).addOnSuccessListener(ref -> {
                        startChatroomIntent();
                    });
                });
    }

    private String chatroomID;

    private void startChatroomIntent() {
        Intent intent = new Intent(context, Chatroom.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("chatroomID", chatroomID);
        context.startActivity(intent);
    }

    @NonNull
    @Override
    public UIDViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.friend_recycler_row, parent, false);
        return new UIDViewHolder(view);
    }

    public static class UIDViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView emailTextView;
//        ImageButton addToFriends;
        ImageButton removeFromFriends;

        public UIDViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.friend_row_username);
            emailTextView = itemView.findViewById(R.id.friend_row_email);
//            addToFriends = itemView.findViewById(R.id.friend_row_add);
            removeFromFriends = itemView.findViewById(R.id.friend_row_remove);
        }
    }
}
