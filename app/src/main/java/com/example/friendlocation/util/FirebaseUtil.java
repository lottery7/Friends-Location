package com.example.friendlocation.util;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    public static String getCurrentUserUID() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static DatabaseReference getCurrentUserDetails() {
        return getUserDetails(getCurrentUserUID());
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
        if (userIds.get(0).equals(getCurrentUserUID())) {
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

    public static void getEventFromSnapshot(DataSnapshot dataSnapshot, Event ev) {
        // TODO: переписать на что-то более короткое, просто getValue(Event.class) не заполняет поля или падает с ошибкой
        ev.name = dataSnapshot.child("name").getValue().toString();
        ev.description = dataSnapshot.child("description").getValue().toString();
        ev.uid = dataSnapshot.child("uid").getValue().toString();
        ev.date = dataSnapshot.child("date").getValue().toString();
        ev.place = new Place(dataSnapshot.child("place").child("description").getValue().toString());
        Double lt = Double.valueOf(dataSnapshot.child("place").child("coordinates").child("first").getValue().toString());
        Double lg = Double.valueOf(dataSnapshot.child("place").child("coordinates").child("second").getValue().toString());
        ev.place.coordinates = new Pair<>(lt, lg);
    }

    public static void makeEventsMarkers(GoogleMap mMap) {
        String uid = getCurrentUserUID();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Event ev = new Event();
                        getEventFromSnapshot(dataSnapshot, ev);
                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng latLng = new LatLng(ev.place.coordinates.getFirst(), ev.place.coordinates.getSecond());
                        markerOptions.position(latLng);
                        mMap.addMarker(markerOptions);
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
