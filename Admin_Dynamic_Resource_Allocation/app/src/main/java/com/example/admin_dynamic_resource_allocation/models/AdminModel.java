package com.example.admin_dynamic_resource_allocation.models;

import java.io.Serializable;

public class AdminModel implements Serializable {
    private String id;
    private String name;
    private String email;

    public AdminModel() {
    }

    public AdminModel(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
