package com.example.friendlocation.utils;

import static java.lang.Math.min;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class FirebaseUtils {
    static FirebaseDatabase firebaseDatabase;
    public static FirebaseDatabase getDatabase() {
        if (firebaseDatabase == null){
            return FirebaseDatabase.getInstance(
                    "https://friendloc-e7399-default-rtdb.europe-west1.firebasedatabase.app/"
            );
        }
        return firebaseDatabase;
    }

    public static void setFirebaseDatabase(FirebaseDatabase newFirebaseDatabase) {
        firebaseDatabase = newFirebaseDatabase;
    }

    public static String getCurrentUserID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(getCurrentUserID());
    }

    public static DatabaseReference getCurrentUserDetails() {
        return getDatabase().getReference("users")
                .child(getCurrentUserID());
    }

    public static boolean addEvent(Event event) {
        DatabaseReference mDatabase = getDatabase().getReference("events");
        if (event.uid == null) {
            event.uid = mDatabase.push().getKey();
        }
        if (event.uid == null) {
            return false;
        }
        mDatabase.child(event.uid).setValue(event);
        for (String userUID : event.membersUID) {
            addEventToUser(event.uid, userUID);
        }
        return true;
    }

    public static boolean saveEvent(Event event) {
        DatabaseReference mDatabase = getDatabase().getReference("events");
        mDatabase.child(event.uid).setValue(event);
        for (String userUID : event.membersUID){
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

    public static StorageReference getProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtils.getCurrentUserID());
    }

    public static StorageReference getEventProfilePicStorageRef(String uid){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child("Event" + uid);
    }

    public static StorageReference getProfilePicStorageRefByUid(String UserUid){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(UserUid);
    }

}
