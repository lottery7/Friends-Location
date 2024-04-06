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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UsersAdapterEvent extends RecyclerView.Adapter<UsersAdapterEvent.ViewHolder>{

    private final LayoutInflater inflater;
    private final List<User> users;

    public UsersAdapterEvent(Context context, List<User> users) {
        this.users = users;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public UsersAdapterEvent.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.user_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersAdapterEvent.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.userIconView.setImageResource(R.drawable.ic_launcher_foreground);
        holder.nameView.setText(user.name);
        holder.mailView.setText(user.email);
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).uid.equals(user.uid)) {
                        users.remove(i);
                        notifyItemRemoved(i);
                        return;
                    }
                }
            }
        });
    }

    public void addUser(User user) {
        if (users.stream().anyMatch((v)->v.uid.equals(user.uid))) {
            return;
        }
        users.add(user);
        notifyItemInserted(users.size()-1);
    }

    public ArrayList<String> getUsersUID() {
        return (ArrayList<String>) users.stream().map((v)->v.uid).collect(Collectors.toList());
    }
    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView userIconView;
        final TextView nameView, mailView;
        final ImageButton deleteBtn;
        ViewHolder(View view){
            super(view);
            userIconView = view.findViewById(R.id.userIcon_imgv);
            nameView = view.findViewById(R.id.userName_tv);
            mailView = view.findViewById(R.id.userEmail_tv);
            deleteBtn = view.findViewById(R.id.deleteUser_imgbtn);
        }
    }
}