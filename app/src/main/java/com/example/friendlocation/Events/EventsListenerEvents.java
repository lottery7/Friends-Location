package com.example.friendlocation.Events;

import static com.example.friendlocation.utils.FirebaseUtils.getCurrentUserID;
import static com.example.friendlocation.utils.FirebaseUtils.getDatabase;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.CalendarView;

import com.example.friendlocation.adapters.EventAdapterEvents;
import com.example.friendlocation.utils.Event;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class EventsListenerEvents {

    public static HashMap<String, ValueEventListener> eventListeners = new HashMap<>();

    public static HashMap<String, Boolean> eventRemoved = new HashMap<>();


    public static void makeEventsList(EventAdapterEvents eventsAdapter, CalendarView calendarView, Context context, Resources resources) {
        String uid = getCurrentUserID();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            Event ev = dataSnapshot.getValue(Event.class);
                            if (eventRemoved.getOrDefault(ev.uid, false)){
                                return;
                            }
                            eventListeners.put(ev.uid, this);
                            eventsAdapter.addEvent(ev);
                        } catch (Exception e) {
                            Log.e("Making marker", e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                String uid = dataSnapshot.getKey();
                getDatabase().getReference("events").child(uid).addValueEventListener(postListener);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                String commentKey = dataSnapshot.getKey();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String commentKey = dataSnapshot.getKey();
                eventRemoved.put(commentKey, true);
                getDatabase().getReference("events").child(uid).removeEventListener(Objects.requireNonNull(eventListeners.get(commentKey)));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                String commentKey = dataSnapshot.getKey();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String commentKey = databaseError.getMessage();
            }
        };
        getDatabase().getReference("users").child(uid).child("events").addChildEventListener(childEventListener);
    }
}
