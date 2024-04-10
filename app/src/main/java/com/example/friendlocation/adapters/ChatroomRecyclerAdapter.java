package com.example.friendlocation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.ChatMessage;
import com.example.friendlocation.R;
import com.example.friendlocation.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatroomRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessage, ChatroomRecyclerAdapter.ChatroomViewHolder> {
    private final Context context;

    public ChatroomRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomViewHolder holder, int position, @NonNull ChatMessage message) {
        if (message.senderId.equals(FirebaseUtil.getCurrentUserUID())) {
            holder.otherCardView.setVisibility(View.GONE);
            holder.cardView.setVisibility(View.VISIBLE);
            holder.messageText.setText(message.message);
        } else {
            holder.cardView.setVisibility(View.GONE);
            holder.otherCardView.setVisibility(View.VISIBLE);
            holder.otherMessageText.setText(message.message);
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

    static class ChatroomViewHolder extends RecyclerView.ViewHolder {
        CardView cardView, otherCardView;
        TextView messageText, otherMessageText;

        public ChatroomViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.user_message_cardview);
            messageText = itemView.findViewById(R.id.user_message_text);

            otherCardView = itemView.findViewById(R.id.other_user_message_cardview);
            otherMessageText = itemView.findViewById(R.id.other_user_message_text);
        }
    }
}
