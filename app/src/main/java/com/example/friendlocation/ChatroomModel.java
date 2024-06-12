package com.example.friendlocation;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatroomModel {
    public String id;
    public List<String> userIds;
    public Timestamp lastMessageDate;
    public String lastMessageSenderId;
    public String lastMessageText;
    public String title;
    public Boolean isGroup;

    public ChatroomModel() {
    }

    public ChatroomModel(
            String id
            , Boolean isGroup
            , List<String> userIds
            , Timestamp lastMessageDate
            , String lastMessageSenderId
            , String lastMessageText
            , String title
    ) {
        this.id = id;
        this.isGroup = isGroup;
        this.userIds = userIds;
        this.lastMessageDate = lastMessageDate;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessageText = lastMessageText;
        this.title = title;
    }


}
