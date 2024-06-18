package com.example.friendlocation.Maps.Listeners;

import static com.example.friendlocation.utils.FirebaseUtils.getCurrentUserID;
import static com.example.friendlocation.utils.FirebaseUtils.getDatabase;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;

import com.example.friendlocation.Maps.Markers.EventMarkerIcon;
import com.example.friendlocation.Maps.Markers.MarkerIcon;
import com.example.friendlocation.utils.Event;
import com.example.friendlocation.utils.Pair;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class MarkerEventsListener {
    private static HashMap<String, Marker> mapMarkers = new HashMap<>();
    private static HashMap<String, ValueEventListener> mapMarkerListeners = new HashMap<>();
    private static HashMap<String, Boolean> mapMarkersIsDelete = new HashMap<>();

    public static void makeEventsMarkers(GoogleMap mMap, Context context, Resources resources) {
        String uid = getCurrentUserID();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            Event ev = dataSnapshot.getValue(Event.class);
                            if (mapMarkersIsDelete.getOrDefault(ev.uid, false)){
                                return;
                            }
                            if (mapMarkers.containsKey(ev.uid)) {
                                mapMarkers.get(ev.uid).remove();
                            }
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(ev.getLatLng());
                            MarkerIcon markerIcon = new MarkerIcon(ev, context, resources);
                            markerOptions.icon(markerIcon.eventMarkerIcon.getBriefMarkerIcon());
                            Marker x = mMap.addMarker(markerOptions);
                            x.setTag(markerIcon);
                            ValueEventListener valueEventListener = this;
                            mapMarkers.put(ev.uid, x);
                            mapMarkersIsDelete.put(ev.uid, false);
                        } catch (Exception e) {
                            Log.e("Making marker", e.getMessage());
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                String uid = dataSnapshot.getKey();
                mapMarkerListeners.put(uid, getDatabase().getReference("events").child(uid).addValueEventListener(postListener));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                String commentKey = dataSnapshot.getKey();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String eventUID = dataSnapshot.getKey();
                getDatabase().getReference("events").child(uid).removeEventListener(mapMarkerListeners.get(eventUID));
                mapMarkers.get(eventUID).remove();
                mapMarkersIsDelete.put(eventUID, true);
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
