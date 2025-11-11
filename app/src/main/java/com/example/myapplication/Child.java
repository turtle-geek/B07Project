package com.example.myapplication;

import java.time.LocalDate;

public class Child extends User{
    private final int parentID;
    private final LocalDate dateOfBirth;
    private String notes;
    private HealthProfile healthProfile;

    public Child(int id, int parentID, String name, String role, String email, String password, LocalDate dateOfBirth) {
        super(id, name, role, email, password);
        this.parentID = parentID;
        this.dateOfBirth = dateOfBirth;
        this.healthProfile = new HealthProfile();
    }

    // Might need overloading for using parent email to login; currently used in createChild in Parent class

    // Public Getters
    public int getParentID()  {
        return parentID;
    }
    // Setter is omitted as parentID shouldn't change after creation.

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    // Setter is omitted as dateOfBirth shouldn't change after creation;
    // For reference, LocalDate.of(int year, int month, int day) may be used for changing if needed

}