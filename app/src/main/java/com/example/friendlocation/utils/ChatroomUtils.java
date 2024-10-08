package com.example.friendlocation.utils;

import androidx.annotation.NonNull;

import com.example.friendlocation.ChatMessage;
import com.example.friendlocation.ChatroomModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ChatroomUtils {
    public static DatabaseReference getOtherUserFromChat(ChatroomModel chatroomModel) {
        if (chatroomModel.userIds.get(0).equals(FirebaseUtils.getCurrentUserID())) {
            return FirebaseUtils.getUserDetails(chatroomModel.userIds.get(1));
        }
        return FirebaseUtils.getUserDetails(chatroomModel.userIds.get(0));
    }

    public static String getChatId(String firstUserId, String secondUserId) {
        if (firstUserId.compareTo(secondUserId) > 0) {
            getChatId(secondUserId, firstUserId);
        }
        return firstUserId + "_" + secondUserId;
    }

    public static ChatroomModel createChatModel(String firstUserId, String secondUserId) {
        return new ChatroomModel(
                getChatId(firstUserId, secondUserId)
                , false
                , new ArrayList<>(Arrays.asList(firstUserId, secondUserId))
                , null
                , null
                , null
                , null
        );
    }

//    public static Task<DocumentReference> sendMessage(@NonNull ChatroomModel chatroom, @NonNull String senderID, @NonNull String message) {
//        chatroom.lastMessageText = message;
//        chatroom.lastMessageDate = Timestamp.now();
//        chatroom.lastMessageSenderId = FirebaseUtils.getCurrentUserID();
//
//        ChatMessage chatMessage = new ChatMessage(
//                message,
//                senderID,
//                chatroom.lastMessageDate
//        );
//
//        return FirebaseUtils
//                .getChatroomMessagesReference(chatroom.id)
//                .add(chatMessage)
//                .addOnSuccessListener(
//                        task -> FirebaseUtils
//                                .getChatroomReference(chatroom.id)
//                                .set(chatroom)
//                );
//
//    }

    public static ChatroomModel createGroupModel(@NonNull List<String> ids) {
        return new ChatroomModel(
                UUID.randomUUID().toString()
                , true
                , ids
                , null
                , null
                , null
                , null
        );
    }
}
