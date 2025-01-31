package com.example.student_dynamic_resource_allocation.models;

import java.io.Serializable;

public class VolunteerModel implements Serializable {
    private String id;
    private String name;
    private String phoneNumber;

    public VolunteerModel() {
    }

    public VolunteerModel(String id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
