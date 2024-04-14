package com.example.friendlocation.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase getDatabase() {
        return FirebaseDatabase.getInstance(
                "https://friendloc-e7399-default-rtdb.europe-west1.firebasedatabase.app/"
        );
    }

    public static FirebaseFirestore getFirestoreDatabase() {
        return FirebaseFirestore.getInstance();
    }

    public static String getCurrentUserID() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static DatabaseReference getCurrentUserDetails(){
        return  getUserDetails(getCurrentUserID());
    }

    public static DatabaseReference getUsersCollection() {
        return getDatabase().getReference("users");
    }

    public static CollectionReference getChatroomsCollection() {
        return getFirestoreDatabase().collection("chatrooms");
    }

    public static DocumentReference getChatroomReference(String id) {
        return getChatroomsCollection().document(id);
    }

    public static CollectionReference getChatroomMessagesReference(String id) {
        return getChatroomReference(id).collection("messages");
    }

    public static DatabaseReference getUserDetails(String id) {
        return getUsersCollection().child(id);
    }

    public static DatabaseReference getOtherUserFromList(List<String> userIds) {
        if (userIds.get(0).equals(getCurrentUserID())) {
            return getUserDetails(userIds.get(1));
        }
        return getUserDetails(userIds.get(0));
    }

    public static String getChatroomId(String email1, String email2) {
        if (email1.hashCode() < email2.hashCode()) {
            return email1 + "_" + email2;
        }
        return email2 + "_" + email1;
    }
}
