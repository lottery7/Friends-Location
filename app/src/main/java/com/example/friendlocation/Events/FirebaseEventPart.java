package com.example.friendlocation.Events;

import static com.example.friendlocation.utils.FirebaseUtils.getCurrentUserID;
import static com.example.friendlocation.utils.FirebaseUtils.getDatabase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.friendlocation.utils.Event;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class FirebaseEventPart {
    public static void exitFromEvent(Event event) {
        getDatabase().getReference("users").child(getCurrentUserID()).child("events").child(event.uid).removeValue((error, ref) -> Log.i("Remove 1", event.uid));
        getDatabase().getReference("events").child(event.uid).child("membersUID").child(getCurrentUserID()).removeValue((error, ref) -> Log.i("Remove 2", event.uid));
    }
}
