package com.example.friendlocation.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;



public class FirebaseUtils {
    public static FirebaseDatabase getDatabase() {
        return FirebaseDatabase.getInstance(
                "https://friendloc-e7399-default-rtdb.europe-west1.firebasedatabase.app/"
        );
    }

    public static String getCurrentUserID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static DatabaseReference getCurrentUserDetails() {
        return getDatabase().getReference("users")
                .child(getCurrentUserID());
    }

    public static boolean addEvent(Event event) {
        DatabaseReference mDatabase = getDatabase().getReference("events");
        event.uid = mDatabase.push().getKey();
        if (event.uid == null) {
            return false;
        }
        mDatabase.child(event.uid).setValue(event);
        for (String userUID : event.membersUID) {
            addEventToUser(event.uid, userUID);
        }
        return true;
    }

    public static boolean addEventToUser(String eventUID, String userUID) {
        DatabaseReference mDatabase = getDatabase().getReference("users");
        mDatabase.child(userUID).child("events").child(eventUID).setValue(true);
        return true;
    }

    public static DatabaseReference getUsersCollection() {
        return getDatabase().getReference("users");
    }

    public static FirebaseFirestore getFirestoreDatabase() {
        return FirebaseFirestore.getInstance();
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
}
