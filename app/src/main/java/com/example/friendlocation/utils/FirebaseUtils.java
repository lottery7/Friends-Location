package com.example.friendlocation.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {
    public static FirebaseDatabase getDatabase() {
        return FirebaseDatabase.getInstance(
                "https://friendloc-e7399-default-rtdb.europe-west1.firebasedatabase.app/"
        );
    }

    public static String getCurrentUserUID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static DatabaseReference getCurrentUserDetails(){
        return getDatabase().getReference("users")
                .child(getCurrentUserUID());
    }

    public static boolean addEvent(Event event) {
        DatabaseReference mDatabase = getDatabase().getReference("events");
        event.uid = mDatabase.push().getKey();
        if (event.uid == null) {
            return false;
        }
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

}
