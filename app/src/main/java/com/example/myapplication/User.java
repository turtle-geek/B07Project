package com.example.myapplication;

public abstract class User {
    private final int id;
    private final String name;
    private final String role;
    private String email;
    private String password;

    public User(int id, String name, String role, String email, String password) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.email = email;
        this.password = password;
    }

    // Public Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    // Setters are omitted to prevent outside modification of ID/Role/Name after creation
    // If modification is needed, you would add public setters here.

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}