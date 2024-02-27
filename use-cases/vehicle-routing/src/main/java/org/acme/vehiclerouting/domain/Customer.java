package org.acme.vehiclerouting.domain;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    private String name;
    private Location location;
    private List<Visit> visits;
    private float rate;
    private int capacity;
    private SensorReading sensorReading;
    public Customer() {
        this.visits = new ArrayList<>();
    }

    
    public List<Visit> getVisits() {
        return visits;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }
    public float getRate() {
        return rate;
    }
    public void setRate(float rate) {
        this.rate = rate;
    }
    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public SensorReading getSensorReading() {
        return sensorReading;
    }


    public void setSensorReading(SensorReading sensorReading) {
        this.sensorReading = sensorReading;
    }


    public Location getLocation() {
        return location;
    }


    public void setLocation(Location location) {
        this.location = location;
    }
    

}
