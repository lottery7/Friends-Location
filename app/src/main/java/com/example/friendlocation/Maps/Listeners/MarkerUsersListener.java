package com.example.friendlocation.Maps.Listeners;

import static com.example.friendlocation.utils.FirebaseUtils.getCurrentUserID;
import static com.example.friendlocation.utils.FirebaseUtils.getDatabase;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.friendlocation.Maps.Markers.MarkerIcon;
import com.example.friendlocation.R;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.HashMap;

public class MarkerUsersListener {
    private static HashMap<String, Marker> mapMarkers = new HashMap<>();


    public static void makeUsersMarkers(GoogleMap mMap, Context context, Resources resources) {
        String uid = getCurrentUserID();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            User user = dataSnapshot.getValue(User.class);
                            if (!user.isVisible) {
                                if (mapMarkers.containsKey(user.id)) {
                                    mapMarkers.get(user.id).remove();
                                    mapMarkers.remove(user.id);
                                }
                                return;
                            }
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(new LatLng(user.coordinates.getFirst(), user.coordinates.getSecond()));
                            FirebaseUtils.getProfilePicStorageRefByUid(user.id).getDownloadUrl()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Uri uri = task.getResult();
                                            Glide.with(context)
                                                    .load(uri)
                                                    .into(new CustomTarget<Drawable>() {
                                                        @Override
                                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                            MarkerIcon markerIcon = new MarkerIcon(user, context, resources, resource);
                                                            try {
                                                                markerOptions.icon(markerIcon.userMarkerIcon.getBriefMarkerIcon());
                                                            } catch (ParseException e) {
                                                                throw new RuntimeException(e);
                                                            }
                                                            mapMarkers.put(user.id, mMap.addMarker(markerOptions));
                                                        }

                                                        @Override
                                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                                            // Handle cleanup if necessary
                                                        }
                                                    });
                                        } else {
                                            MarkerIcon markerIcon = new MarkerIcon(user, context, resources, ContextCompat.getDrawable(context, R.drawable.event_icon_mark));
                                            try {
                                                markerOptions.icon(markerIcon.userMarkerIcon.getBriefMarkerIcon());
                                            } catch (ParseException e) {
                                                Log.e("aa", "asc");
                                            }
                                            mapMarkers.put(user.id, mMap.addMarker(markerOptions));
                                        }
                                    });
                        } catch (Exception e) {
                            Log.e("Making marker", e.getMessage());
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                String uid = dataSnapshot.getKey();
                getDatabase().getReference("users").child(uid).addValueEventListener(postListener);
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
        getDatabase().getReference("users").child(uid).child("usersWithKnownLocationUID").addChildEventListener(childEventListener);
    }
}
