package com.example.friendlocation;

import com.google.firebase.Timestamp;

public class ChatMessage {
    public String message;
    public String senderId;
    public Timestamp date;

    public ChatMessage() {
    }

    public ChatMessage(String message, String senderId, Timestamp date) {
        this.message = message;
        this.senderId = senderId;
        this.date = date;
    }
}
