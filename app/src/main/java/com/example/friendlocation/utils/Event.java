package com.example.friendlocation.utils;

import static java.lang.System.*;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;

public class Event {
    public static long MAX_DESCRIPTION_LINES = 5;
    public static long MAX_DESCRIPTION_LINE_SIZE = 50;
    public String uid;
    public String name;
    public String date;
    public String description;

    public Place place;
    public ArrayList<String> membersUID;

    public Event() {
    }

    public Event(String uid) {
        this.uid = uid;
    }

    public Event(String name, String date, String description) {
        this.name = name;
        this.date = date;
        this.description = description;
    }

    public Event(String uid, String name, String date, String description) {
        this(name, date, description);
        this.uid = uid;
    }

    public Event(String uid, String name, String date, String description, Place place) {
        this(uid, name, date, description);
        this.place = place;
    }

    public LatLng getLatLng() {
        return new LatLng(place.coordinates.getFirst(), place.coordinates.getSecond());
    }

    public int countLinesInDescription(){
        String[] x = description.split(lineSeparator());
        return (int) Arrays.stream(description.split(lineSeparator())).count();
    }

    public long maxLineSizeInDescription(){
        return Arrays.stream(description.split(lineSeparator())).map(String::length).max(Integer::compareTo).orElse(0);
    }
}
