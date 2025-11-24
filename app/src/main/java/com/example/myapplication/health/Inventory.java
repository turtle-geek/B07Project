package com.example.myapplication.health;

import java.util.ArrayList;

public class Inventory {
    private ArrayList<InventoryItem> inventory;

    public Inventory() {
        inventory = new ArrayList<>();
    }

    public void addItem(InventoryItem medicine) {
        inventory.add(medicine);
    }

    public boolean useMedicine(int index, double amount) {
        if (index < 0 || index >= inventory.size())
            return false;
        InventoryItem medicine = inventory.get(index);
        if (amount > medicine.getAmount())
            return false;
        else {
            medicine.setAmount(medicine.getAmount() - amount);
            if (medicine.getAmount() == 0)
                inventory.remove(medicine);
            return true;
        }
    }

    
}