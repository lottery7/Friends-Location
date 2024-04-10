package com.example.friendlocation.util;

public class Place {
    public String description;

    public Pair<Double, Double> coordinates;

    public Place(String place, Pair<Double, Double> coordinates) {
        this.description = place;
        this.coordinates = coordinates;
    }

    public Place(Pair<Double, Double> coordinates) {
        this.coordinates = coordinates;
    }

    public Place(String place) {
        this.description = place;
    }

}
