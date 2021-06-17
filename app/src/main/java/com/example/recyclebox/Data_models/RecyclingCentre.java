package com.example.recyclebox.Data_models;

import java.util.List;

public class RecyclingCentre {
    private String name;
    private String address;
    private String postcode;
    private List<String> materials;
    private double latitude;
    private double longitude;

    public RecyclingCentre() {
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public String getPostcode() {
        return postcode;
    }
    public List<String> getMaterials() {
        return materials;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    public void setMaterials(List<String> materials) {
        this.materials = materials;
    }
}
