package com.example.friendlocation.utils;

import java.util.ArrayList;

public class User {
    public String name;
    public String email;
    public String id;
    public int right;
    public Pair<Double, Double> coordinates = null;

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String name, String email, String id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public User(String id, Integer right) {
        this.id = id;
        this.right = right;
    }
}

