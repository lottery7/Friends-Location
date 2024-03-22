package com.example.friendlocation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.TextView;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.utils.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class SearchUserRecyclerAdapter extends FirebaseRecyclerAdapter<User, SearchUserRecyclerAdapter.UserViewHolder> {

    private OnUserClickListener onUserClickListener;

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.onUserClickListener = listener;
    }
    public SearchUserRecyclerAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User user) {
        holder.usernameTextView.setText(user.name);
        holder.usermailTextView.setText(user.email);
        holder.user = user;
        holder.itemView.setOnClickListener((v) -> {
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(user);
            }
        });
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.search_user_recycler, parent, false);
        return new UserViewHolder(view);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView usermailTextView;
        User user;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.search_user_row_username);
            usermailTextView = itemView.findViewById(R.id.search_user_row_usermail);
        }
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

}
