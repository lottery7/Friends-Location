package com.example.friendlocation.Maps;

import android.content.Context;
import android.content.res.Resources;

import com.example.friendlocation.utils.Event;
import com.google.android.gms.maps.model.BitmapDescriptor;

import java.text.ParseException;

public class MarkerIcon {
    private BriefMarkerIcon briefMarkerIcon;
    private FullMarkerIcon fullMarkerIcon;
    private int clickCount = 0;

    public MarkerIcon(Event event, Context context, Resources resources) {
        this.briefMarkerIcon = new BriefMarkerIcon(event, context, resources);
        this.fullMarkerIcon = new FullMarkerIcon(event, context, resources);
    }

    public BitmapDescriptor getBriefMarkerIcon() throws ParseException {
        return briefMarkerIcon.getBriefMarkerIcon();
    }

    public BitmapDescriptor getFullMarkerIcon() throws ParseException {
        return fullMarkerIcon.getFullMarkerIcon();
    }

    public int getClickCount() {
        return clickCount;
    }

    public void click(){
        clickCount = 1;
    }

    public void reset(){
        clickCount = 0;
    }
}
