package com.example.friendlocation;

import java.util.ArrayList;

public class Event {
    public String name;
    public int date;
    public String description;
    public ArrayList<User> membersUID;

    public Event() {
    }

    public Event(String name, Integer date, String description) {
        this.name = name;
        this.date = date;
        this.description = description;
    }
}
