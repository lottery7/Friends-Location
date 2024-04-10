package com.example.friendlocation.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class UserAdapterSearch extends FirebaseRecyclerAdapter<User, UserAdapterSearch.UserViewHolder> {

    private OnUserClickListener onUserClickListener;

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.onUserClickListener = listener;
    }
    public UserAdapterSearch(@NonNull FirebaseRecyclerOptions<User> options) {
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
                .inflate(R.layout.user_adapter, parent, false);
        return new UserViewHolder(view);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView usermailTextView;
        User user;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.userName_tv);
            usermailTextView = itemView.findViewById(R.id.userEmail_tv);
            itemView.findViewById(R.id.deleteUser_imgbtn).setVisibility(View.INVISIBLE);
        }
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

}
