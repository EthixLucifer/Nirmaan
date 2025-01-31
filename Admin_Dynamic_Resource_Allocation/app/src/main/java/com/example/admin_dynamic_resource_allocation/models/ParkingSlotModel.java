package com.example.admin_dynamic_resource_allocation.models;

import java.io.Serializable;

public class ParkingSlotModel implements Serializable {
    private String id;
    private int isAvailable;
    private String vehicleNo;
    private String prn;
    private LocationModel currentLocation;

    public ParkingSlotModel() {
    }

    public ParkingSlotModel(String id, int isAvailable, String vehicleNo, String prn, LocationModel currentLocation) {
        this.id = id;
        this.isAvailable = isAvailable;
        this.vehicleNo = vehicleNo;
        this.prn = prn;
        this.currentLocation = currentLocation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(int isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getPrn() {
        return prn;
    }

    public void setPrn(String prn) {
        this.prn = prn;
    }

    public LocationModel getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LocationModel currentLocation) {
        this.currentLocation = currentLocation;
    }
}
