package com.example.friendlocation.utils;

public class Place {
    public String description;

    public Pair<Double, Double> coordinates;

    public Place() {
        this.description = "default";
    }

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
