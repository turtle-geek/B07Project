package com.example.myapplication.health;

import java.time.LocalDateTime;

public class InventoryItem {
    private double amount;
    private final double capacity;
    private final LocalDateTime purchaseDate;
    private final LocalDateTime expiryDate;

    public InventoryItem(double amount, double capacity, LocalDateTime purchaseDate, LocalDateTime expiryDate) {
        this.amount = amount;
        this.capacity = capacity;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getCapacity() {
        return capacity;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public boolean lowVolumeAlert() {
        return amount / capacity <= 0.2;
    }

    public boolean expiryAlert() {
        return LocalDateTime.now().compareTo(expiryDate.minusWeeks(4)) > 0; // 4 weeks before expiry
    }
}
