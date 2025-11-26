package com.example.myapplication.health;

import java.time.LocalDateTime;

public class MedicineUsageLog {
    private String medicineName;
    private double dosageAmount;
    private LocalDateTime timestamp;

    public MedicineUsageLog(String medicineName, double dosageAmount, LocalDateTime timestamp) {
        this.medicineName = medicineName;
        this.dosageAmount = dosageAmount;
        this.timestamp = timestamp;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public double getDosageAmount() {
        return dosageAmount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Medicine: " + medicineName + ", Dosage: " + dosageAmount + ", Timestamp: " + timestamp;
    }
}
