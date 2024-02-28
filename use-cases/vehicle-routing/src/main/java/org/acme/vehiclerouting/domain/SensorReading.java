package org.acme.vehiclerouting.domain;

import java.time.LocalDate;

public class SensorReading {
    private LocalDate date;
    private int value;

    public SensorReading() {
    }

    public SensorReading(LocalDate date, int value) {
        this.date = date;
        this.value = value;
    }

    public LocalDate getDate() {
        return date;

    }

    public int getValue() {
        return value;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
