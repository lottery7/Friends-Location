package com.example.friendlocation.utils;

import java.util.ArrayList;

public class Event {
    public String uid;
    public String name;
    public String date;
    public String description;

    public Place place;
    public ArrayList<String> membersUID;

    public Event() {
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
}
