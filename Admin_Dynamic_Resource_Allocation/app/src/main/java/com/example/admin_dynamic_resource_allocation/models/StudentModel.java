package com.example.admin_dynamic_resource_allocation.models;

public class StudentModel {
    private String name;
    private String id;
    private String email;
    private String adminEmail;
    private String phoneNumber;
    private LocationModel currentLocation;
    private int isVehicleParked;
    private LocationModel vehicleLocation;
    private String randId;

    public StudentModel() {
    }

    public StudentModel(String name, String id, String email, String adminEmail, String phoneNumber, LocationModel currentLocation, int isVehicleParked, LocationModel vehicleLocation, String randId) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.adminEmail = adminEmail;
        this.phoneNumber = phoneNumber;
        this.currentLocation = currentLocation;
        this.isVehicleParked = isVehicleParked;
        this.vehicleLocation = vehicleLocation;
        this.randId = randId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocationModel getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LocationModel currentLocation) {
        this.currentLocation = currentLocation;
    }

    public int getIsVehicleParked() {
        return isVehicleParked;
    }

    public void setIsVehicleParked(int isVehicleParked) {
        this.isVehicleParked = isVehicleParked;
    }

    public LocationModel getVehicleLocation() {
        return vehicleLocation;
    }

    public void setVehicleLocation(LocationModel vehicleLocation) {
        this.vehicleLocation = vehicleLocation;
    }

    public String getRandId() {
        return randId;
    }

    public void setRandId(String randId) {
        this.randId = randId;
    }
}
