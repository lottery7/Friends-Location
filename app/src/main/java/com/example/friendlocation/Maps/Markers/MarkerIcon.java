package com.example.friendlocation.Maps.Markers;

import android.content.Context;
import android.content.res.Resources;

import com.example.friendlocation.utils.Event;
import com.example.friendlocation.utils.User;

public class MarkerIcon {
    public EventMarkerIcon eventMarkerIcon;
    public UserMarkerIcon userMarkerIcon;
    private MarkerType markerType;
    public Event event;

    public MarkerIcon(Event event, Context context, Resources resources) {
        this.eventMarkerIcon = new EventMarkerIcon(event, context, resources);
        markerType = MarkerType.EVENT;
        this.event = event;
    }

    public MarkerIcon(User user, Context context, Resources resources) {
        this.userMarkerIcon = new UserMarkerIcon(user, context, resources);
        markerType = MarkerType.USER;
    }

    private enum MarkerType {
        USER,
        EVENT
    }

    public boolean isEvent(){
        return markerType == MarkerType.EVENT;
    }
    public boolean isUser(){
        return markerType == MarkerType.USER;
    }
}
