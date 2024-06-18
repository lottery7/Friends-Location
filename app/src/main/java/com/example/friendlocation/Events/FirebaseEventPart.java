package com.example.friendlocation.Events;

import static com.example.friendlocation.utils.FirebaseUtils.getCurrentUserID;
import static com.example.friendlocation.utils.FirebaseUtils.getDatabase;
import static com.example.friendlocation.utils.FirebaseUtils.saveEvent;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.friendlocation.utils.Event;
import com.example.friendlocation.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class FirebaseEventPart {
    public static void exitFromEvent(Event event) {
        getDatabase().getReference("users").child(getCurrentUserID()).child("events").child(event.uid).removeValue((error, ref) -> Log.i("Remove 1", event.uid));
        getDatabase().getReference("events").child(event.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event ev = snapshot.getValue(Event.class);
                String userUID = getCurrentUserID();
                ev.membersUID.removeIf(p->p.equals(userUID));
                saveEvent(ev);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
