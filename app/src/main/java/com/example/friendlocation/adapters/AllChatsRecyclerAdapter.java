package com.example.friendlocation.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.Chatroom;
import com.example.friendlocation.ChatroomModel;
import com.example.friendlocation.R;
import com.example.friendlocation.utils.ChatroomUtils;
import com.example.friendlocation.utils.User;
import com.example.friendlocation.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AllChatsRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, AllChatsRecyclerAdapter.ChatroomViewHolder> {
    private final Context context;

    public AllChatsRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomViewHolder holder, int position, @NonNull ChatroomModel chatroomModel) {
        DateFormat format = new SimpleDateFormat("HH:mm");
        holder.lastMessageTime.setText(
                format.format(chatroomModel.lastMessageDate.toDate())
        );

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chatroom.class);
            intent.putExtra("chatroomID", chatroomModel.id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        if (chatroomModel.isGroup) {
            holder.title.setText(chatroomModel.title);
        } else {
            ChatroomUtils
                    .getOtherUserFromChat(chatroomModel)
                    .child("name")
                    .get()
                    .addOnSuccessListener(snapshot1 -> {
                holder.title.setText(snapshot1.getValue(String.class));
            });
        }
        holder.lastMessageText.setText(chatroomModel.lastMessageText);
        holder.textBeforeLastMessage.setTextColor(context.getColor(R.color.primary));

        String lastUID = chatroomModel.lastMessageSenderId;
        if (lastUID == null) {
            holder.textBeforeLastMessage.setVisibility(View.GONE);
        } else if (lastUID.equals(FirebaseUtils.getCurrentUserID())) {
            holder.textBeforeLastMessage.setVisibility(View.VISIBLE);
            holder.textBeforeLastMessage.setText("You:");
        } else {
            FirebaseUtils.getUserDetails(lastUID).get().addOnSuccessListener(dataSnapshot -> {
                User lastUser = dataSnapshot.getValue(User.class);
                holder.textBeforeLastMessage.setVisibility(View.VISIBLE);
                assert lastUser != null;
                holder.textBeforeLastMessage.setText(lastUser.name.concat(":"));
            });
        }

    }

    @NonNull
    @Override
    public ChatroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.all_chats_recycler_row, parent, false);
        return new ChatroomViewHolder(view);
    }

    static class ChatroomViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView textBeforeLastMessage;
        TextView lastMessageText;
        TextView lastMessageTime;
        RelativeLayout profilePic;

        public ChatroomViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.chat_preview_title);
            textBeforeLastMessage = itemView.findViewById(R.id.chat_preview_text_before_message);
            lastMessageText = itemView.findViewById(R.id.chat_preview_last_message);
            lastMessageTime = itemView.findViewById(R.id.chat_preview_last_message_time);
            profilePic = itemView.findViewById(R.id.chat_preview_profile_pic);
        }
    }
}
