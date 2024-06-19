package com.example.friendlocation.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.ChatMessage;
import com.example.friendlocation.R;
import com.example.friendlocation.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Firebase;

public class ChatroomRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessage, ChatroomRecyclerAdapter.ChatroomViewHolder> {
    private final Context context;

    public ChatroomRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomViewHolder holder, int position, @NonNull ChatMessage message) {
        if (message.senderId.equals(FirebaseUtils.getCurrentUserID())) {
            holder.messageText.setText(message.message);

            holder.cardView.setVisibility(View.VISIBLE);
            holder.otherCardView.setVisibility(View.GONE);
            holder.otherProfilePictureLayout.setVisibility(View.GONE);
            holder.otherUserName.setVisibility(View.GONE);
        } else {
            holder.otherMessageText.setText(message.message);

            holder.cardView.setVisibility(View.GONE);
            holder.otherCardView.setVisibility(View.VISIBLE);
            holder.otherProfilePictureLayout.setVisibility(View.VISIBLE);
            FirebaseUtils
                    .getUserDetails(message.senderId)
                    .child("name")
                    .get()
                    .addOnSuccessListener(dataSnapshot -> {
                                String otherUserName = dataSnapshot.getValue(String.class);
                                holder.otherUserName.setText(otherUserName);
                                holder.otherUserName.setVisibility(View.VISIBLE);
                            }
                    );
        }
    }

    @NonNull
    @Override
    public ChatroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatroomViewHolder(view);
    }

    public static class ChatroomViewHolder extends RecyclerView.ViewHolder {
        CardView cardView, otherCardView;
        RelativeLayout otherProfilePictureLayout;
        TextView messageText, otherMessageText, otherUserName;

        public ChatroomViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.user_message_cardview);
            messageText = itemView.findViewById(R.id.user_message_text);

            otherProfilePictureLayout = itemView.findViewById(R.id.other_message_profile_picture);
            otherCardView = itemView.findViewById(R.id.other_user_message_cardview);
            otherMessageText = itemView.findViewById(R.id.other_user_message_text);
            otherUserName = itemView.findViewById(R.id.other_user_name);
        }
    }
}
