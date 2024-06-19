package com.example.friendlocation.utils;

import static java.lang.System.*;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Event {
    public static long MAX_DESCRIPTION_LINES = 5;
    public static long MAX_DESCRIPTION_LINE_SIZE = 50;
    public String uid;
    public String name;
    public String owner;
    public String date;
    public String description;

    public Place place;

    public String chatUID;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(uid, event.uid) && Objects.equals(chatUID, event.chatUID) && Objects.equals(name, event.name) && Objects.equals(owner, event.owner) && Objects.equals(date, event.date) && Objects.equals(description, event.description) && Objects.equals(place, event.place) && Objects.equals(membersUID, event.membersUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, name, owner, date, description, place, chatUID, membersUID);
    }
}
