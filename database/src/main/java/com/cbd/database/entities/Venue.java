package com.cbd.database.entities;

import java.util.List;
import java.util.Objects;

public class Venue {

    private String uid;

    private String name;
    private Double latitude;
    private Double longitude;
    private List<String> activities;
    private String pictureReference;

    public Venue() {

    }

    public Venue(String uid, String name, Double latitude, Double longitude, List<String> activities, String pictureReference) {
        this.uid = uid;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.activities = activities;
        this.pictureReference = pictureReference;
    }

    public Venue(String name, Double latitude, Double longitude, List<String> activities, String pictureReference) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.activities = activities;
        this.pictureReference = pictureReference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<String> getActivities() {
        return activities;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }

    public String getPictureReference() {
        return this.pictureReference;
    }

    public void setPictureReference(String pictureReference) {
        this.pictureReference = pictureReference;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venue venue = (Venue) o;
        return Objects.equals(uid, venue.uid) &&
                Objects.equals(name, venue.name) &&
                Objects.equals(latitude, venue.latitude) &&
                Objects.equals(longitude, venue.longitude);
    }
}
