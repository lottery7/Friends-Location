package com.example.friendlocation.utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.R;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final List<User> users;

    public UsersAdapter(Context context, List<User> users) {
        this.users = users;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.events_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.userIconView.setImageResource(R.drawable.ic_launcher_foreground);
        holder.nameView.setText(user.name);
        holder.mailView.setText(user.email);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView userIconView;
        final TextView nameView, mailView;
        final ImageButton deleteView;
        ViewHolder(View view){
            super(view);
            userIconView = view.findViewById(R.id.userIcon);
            nameView = view.findViewById(R.id.userName);
            mailView = view.findViewById(R.id.userEmail);
            deleteView = view.findViewById(R.id.deleteUser);
        }
    }
}