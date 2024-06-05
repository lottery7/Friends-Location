package com.example.friendlocation.Maps.Markers;

import android.content.Context;
import android.content.res.Resources;

import com.example.friendlocation.utils.Event;
import com.google.android.gms.maps.model.BitmapDescriptor;

import java.text.ParseException;

public class EventMarkerIcon {
    private BriefEventMarkerIcon briefEventMarkerIcon;
    private FullEventMarkerIcon fullEventMarkerIcon;
    private int clickCount = 0;

    public EventMarkerIcon(Event event, Context context, Resources resources) {
        this.briefEventMarkerIcon = new BriefEventMarkerIcon(event, context, resources);
        this.fullEventMarkerIcon = new FullEventMarkerIcon(event, context, resources);
    }

    public BitmapDescriptor getBriefMarkerIcon() throws ParseException {
        return briefEventMarkerIcon.getBriefMarkerIcon();
    }

    public BitmapDescriptor getFullMarkerIcon() throws ParseException {
        return fullEventMarkerIcon.getFullMarkerIcon();
    }
}
