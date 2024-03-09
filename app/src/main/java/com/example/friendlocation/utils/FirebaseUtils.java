package com.example.friendlocation.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {
    public static String getCurrentUserUID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static DatabaseReference getCurrentUserDetails(){
        return FirebaseDatabase
                .getInstance("https://friendloc-e7399-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users")
                .child(getCurrentUserUID());
    }
}
