package com.example.friendlocation;

public class User {
    public String name;
    public String email;
    public String uid;
    public int right;

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }

    public User(String uid, Integer right) {
        this.uid = uid;
        this.right = right;
    }
}

