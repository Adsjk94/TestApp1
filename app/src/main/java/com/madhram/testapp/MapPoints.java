package com.madhram.testapp;

class MapPoints {

    private String id;
    private String name;
    private String country;
    private Double lat;
    private Double lon;

    MapPoints(String id, String name, String country, Double lat, Double lon){
        this.id = id;
        this.name = name;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }
}