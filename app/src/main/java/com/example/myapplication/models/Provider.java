package com.example.myapplication.models;
import java.util.ArrayList;

public class Provider extends User{
    private final ArrayList<Integer> patients;

    public Provider(String id, String name, String email) {
        super(id, name);
        this.email = email;
        patients = new ArrayList<>(); // Using diamond operator
    }

    /** This method adds an existing patient into the list of the provider's patients.
     * @param id of the patient
     */
    public void addPatient(int id) {
        patients.add(id); // Java autoboxing handles the conversion from int to Integer
    }

    // Public Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Integer> getPatients() {
        return patients;
    }

    // Setter for patients is omitted; the addPatient method controls modification.
}