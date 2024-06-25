package com.example.friendlocation.Maps.Markers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.example.friendlocation.utils.Event;
import com.google.android.gms.maps.model.BitmapDescriptor;

import java.text.ParseException;

public class EventMarkerIcon {
    private BriefEventMarkerIcon briefEventMarkerIcon;
    private FullEventMarkerIcon fullEventMarkerIcon;
    private int clickCount = 0;

    public EventMarkerIcon(Event event, Context context, Resources resources, Drawable icon) {
        this.briefEventMarkerIcon = new BriefEventMarkerIcon(event, context, resources, icon);
        this.fullEventMarkerIcon = new FullEventMarkerIcon(event, context, resources, icon);
    }

    public BitmapDescriptor getBriefMarkerIcon() throws ParseException {
        return briefEventMarkerIcon.getBriefMarkerIcon();
    }

    public BitmapDescriptor getFullMarkerIcon() throws ParseException {
        return fullEventMarkerIcon.getFullMarkerIcon();
    }
}
