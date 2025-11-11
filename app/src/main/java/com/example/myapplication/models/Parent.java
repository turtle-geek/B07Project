package com.example.myapplication.models;
import java.util.ArrayList;

public class Parent extends User{
    private final ArrayList<Child> children;
    private int providerID;

    public Parent(String id, String name, String email) {
        super(id, name);
        this.email = email;
        this.children = new ArrayList<>(); // Using diamond operator for cleaner code
    }

    public void createChild(String idChild, String idParent, String name) {
        Child child = new Child(idChild, idParent, name);
        children.add(child);
    }

    public void addProvider(int providerID) {
        this.providerID = providerID; // setter logic
    }

    // Public Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Child> getChildren() {
        // Return a copy or an unmodifiable list for better encapsulation if needed,
        // but for now, returning the reference allows access to the list contents.
        return children;
    }

    public int getProviderID() {
        return providerID;
    }
    // Note: addProvider(int) serves as the setter for providerID
}