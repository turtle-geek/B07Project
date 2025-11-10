package com.example.myapplication.models;

public class Child extends User{
    int parentID;
    String email;
    String password;

    public Child(int id, int parentID, String name, String role) {
        super(id, name, role);
        this.parentID = parentID;
        // Potentially adding parent's email and password here too?
    }

    // Overloaded method for child to create their own profile
    public Child(int id, int parentID, String name, String role, String email, String password){
        super(id, name, role);
        this.parentID = parentID;
        this.email = email;
        this.password = password;
    }

    // Public Getters
    public int getParentID() {
        return parentID;
    }

    // Setter is omitted as parentID shouldn't change after creation.
}
