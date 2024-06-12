package com.example.friendlocation.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class SearchUserRecyclerAdapter extends FirebaseRecyclerAdapter<User, SearchUserRecyclerAdapter.UserViewHolder> {
    private final Context context;

    public SearchUserRecyclerAdapter(@NonNull FirebaseRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User user) {
        String username = user.name;
        if (FirebaseUtils.getCurrentUserID().equals(user.id)) {
            username += " (me)";
        }
        holder.usernameTextView.setText(username);
        holder.emailTextView.setText(user.email);

        holder.itemView.setOnClickListener(view -> {
            goToChatWith(user);
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
                        Log.v("DEBUG", "not a null");
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
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserViewHolder(view);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView emailTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.search_user_row_username);
            emailTextView = itemView.findViewById(R.id.search_user_row_email);
        }
    }
}
