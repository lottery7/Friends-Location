package com.example.friendlocation.utils;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Objects.equals(description, place.description) && Objects.equals(coordinates, place.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, coordinates);
    }
}
