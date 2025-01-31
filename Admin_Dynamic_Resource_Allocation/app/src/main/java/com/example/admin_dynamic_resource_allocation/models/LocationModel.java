package com.example.admin_dynamic_resource_allocation.models;

import java.io.Serializable;

public class LocationModel implements Serializable {
    private double lat;
    private double lang;

    public LocationModel() {
    }

    public LocationModel(double lat, double lang) {
        this.lat = lat;
        this.lang = lang;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }
}