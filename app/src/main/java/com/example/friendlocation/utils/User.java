package com.example.friendlocation.utils;

import java.util.ArrayList;
import java.util.Objects;

public class User {
    public String name;
    public String email;
    public String id;
    public int right;
    public boolean isVisible = true;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return right == user.right && Objects.equals(name, user.name) && Objects.equals(email, user.email) && Objects.equals(id, user.id) && Objects.equals(coordinates, user.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, id, right, coordinates);
    }
}

