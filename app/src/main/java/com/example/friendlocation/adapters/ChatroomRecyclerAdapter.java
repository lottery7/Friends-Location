package com.example.friendlocation.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.ChatMessage;
import com.example.friendlocation.R;
import com.example.friendlocation.utils.AndroidUtils;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.User;
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

            holder.showCurrentUser();
            holder.hideOtherUser();
        } else {
            holder.hideCurrentUser();
            holder.showOtherUser();
            holder.otherMessageText.setText(message.message);
            FirebaseUtils
                    .getUserDetails(message.senderId)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        User otherUser = snapshot.getValue(User.class);
                        if (otherUser == null) {
                            return;
                        }

                        holder.otherUserName.setText(otherUser.name);
                        FirebaseUtils
                                .getProfilePicStorageRefByUid(otherUser.id)
                                .getDownloadUrl()
                                .addOnSuccessListener(
                                        uri -> AndroidUtils.setProfilePic(
                                                this.context
                                                , uri
                                                , holder.otherUserProfilePic
                                        )
                                );
                    });
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
        ImageView otherUserProfilePic;
        RelativeLayout otherUserProfilePicLayout;
        TextView messageText, otherMessageText, otherUserName;

        private void changeOtherUserVisibility(int visibility) {
            otherCardView.setVisibility(visibility);
            otherUserProfilePicLayout.setVisibility(visibility);
        }

        private void changeCurrentUserVisibility(int visibility) {
            cardView.setVisibility(visibility);
        }

        public void hideOtherUser() {
            changeOtherUserVisibility(View.GONE);
        }

        public void showOtherUser() {
            changeOtherUserVisibility(View.VISIBLE);
        }

        public void hideCurrentUser() {
            changeCurrentUserVisibility(View.GONE);
        }

        public void showCurrentUser() {
            changeCurrentUserVisibility(View.VISIBLE);
        }

        public ChatroomViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.user_message_cardview);
            messageText = itemView.findViewById(R.id.user_message_text);

            otherUserProfilePic = itemView.findViewById(R.id.user_photo_iv);
            otherCardView = itemView.findViewById(R.id.other_user_message_cardview);
            otherMessageText = itemView.findViewById(R.id.other_user_message_text);
            otherUserName = itemView.findViewById(R.id.other_user_name);
            otherUserProfilePicLayout = itemView.findViewById(R.id.other_user_profile_picture);
        }
    }
}
