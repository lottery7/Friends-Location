package com.example.friendlocation;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatroomModel {
    public String id;
    public List<String> userIds;
    public Timestamp lastMessageDate;
    public String lastMessageSenderId;
    public String lastMessageText;

    public ChatroomModel() {
    }

    public ChatroomModel(String id, List<String> userIds, Timestamp lastMessageDate, String lastMessageSenderId, String lastMessageText) {
        this.id = id;
        this.userIds = userIds;
        this.lastMessageDate = lastMessageDate;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessageText = lastMessageText;
    }


}
