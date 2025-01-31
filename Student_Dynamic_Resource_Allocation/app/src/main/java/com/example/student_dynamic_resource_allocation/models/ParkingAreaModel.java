package com.example.student_dynamic_resource_allocation.models;

import java.io.Serializable;
import java.util.List;

public class ParkingAreaModel implements Serializable {
    private String id;
    private String name;
    private LocationModel entryLocation;
    private LocationModel exitLocation;
    private int totalSlots;
    private int availableSlots;
    private List<ParkingSlotModel> parkingSlots;

    public ParkingAreaModel() {
    }

    public ParkingAreaModel(String id, String name, LocationModel entryLocation, LocationModel exitLocation, int totalSlots, int availableSlots, List<ParkingSlotModel> parkingSlots) {
        this.id = id;
        this.name = name;
        this.entryLocation = entryLocation;
        this.exitLocation = exitLocation;
        this.totalSlots = totalSlots;
        this.availableSlots = availableSlots;
        this.parkingSlots = parkingSlots;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationModel getEntryLocation() {
        return entryLocation;
    }

    public void setEntryLocation(LocationModel entryLocation) {
        this.entryLocation = entryLocation;
    }

    public LocationModel getExitLocation() {
        return exitLocation;
    }

    public void setExitLocation(LocationModel exitLocation) {
        this.exitLocation = exitLocation;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(int availableSlots) {
        this.availableSlots = availableSlots;
    }

    public List<ParkingSlotModel> getParkingSlots() {
        return parkingSlots;
    }

    public void setParkingSlots(List<ParkingSlotModel> parkingSlots) {
        this.parkingSlots = parkingSlots;
    }
}
