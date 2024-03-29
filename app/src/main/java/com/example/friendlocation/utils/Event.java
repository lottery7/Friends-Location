package com.example.friendlocation.utils;

import java.util.ArrayList;

public class Event {
    public String uid;
    public String name;
    public String date;
    public String description;
    public Pair<Float, Float> coordinates;
    public ArrayList<String> membersUID;

    public Event() {
    }

    public Event(String name, String date, String description) {
        this.name = name;
        this.date = date;
        this.description = description;
    }

    public Event(String uid, String name, String date, String description) {
        this.uid = uid;
        this.name = name;
        this.date = date;
        this.description = description;
    }

    public Event(String uid, String name, String date, String description, Pair<Float, Float> coordinates) {
        this.uid = uid;
        this.name = name;
        this.date = date;
        this.description = description;
        this.coordinates = coordinates;
    }
}
