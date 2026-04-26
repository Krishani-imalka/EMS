package com.Java.EMS.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "venues")
public class Venue {
    @Id
    @Column(name = "v_name",length = 200,nullable = false)
    private String vName;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "location", length = 50, nullable = false)
    private String location;

    public Venue() {
    }

    public Venue(String vName, String description, String location) {
        this.vName = vName;
        this.description = description;
        this.location = location;
    }

    public String getvName() {
        return vName;
    }

    public void setvName(String vName) {
        this.vName = vName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
