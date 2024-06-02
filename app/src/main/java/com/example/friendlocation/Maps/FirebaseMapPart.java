package com.example.friendlocation.Maps;

import static com.example.friendlocation.utils.FirebaseUtils.getCurrentUserID;
import static com.example.friendlocation.utils.FirebaseUtils.getDatabase;
import static com.example.friendlocation.utils.FirebaseUtils.getFirestoreDatabase;

import com.example.friendlocation.utils.Pair;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;

public class FirebaseMapPart {

    public static void updateLocation(Pair<Double, Double> coordinates) {
        String userUID = getCurrentUserID();
        DatabaseReference mDatabase = getDatabase().getReference("users");
        mDatabase.child(userUID).child("coordinates").setValue(coordinates);
    }
}
