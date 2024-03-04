package org.acme.vehiclerouting.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
@JsonIdentityInfo(scope = Customer.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Customer {
    @PlanningId
    private long id;

    private String name;
    private Location location;
    private float rate;
    private int capacity;
    private SensorReading sensorReading;

    public Customer() {
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


    public void setId(long i) {
       this.id = i;
    }
    
    public long getId() {
        return id;
    }

}
