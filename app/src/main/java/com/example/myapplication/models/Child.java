package com.example.myapplication.models;

import android.util.Log;
import java.time.LocalDate;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.myapplication.auth.AuthMan.addToDatabase;

public class Child extends User{

    final String parentID;
    private LocalDate dateOfBirth;
    private String notes;
    private HealthProfile healthProfile;

    public Child(String id, String parentID, String name, String parentEmail, String role) {
        super(id, name, role);
        this.parentID = parentID;
        this.emailUsername = parentEmail;
        // Add to firebase firestore
        addToDatabase(this);
    }

    // Public Setters
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public void setDOB(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setHealthProfile(HealthProfile profile){
        this.healthProfile = profile;
    }

    public HealthProfile getHealthProfile(){
        return healthProfile;
    }

    // Public Getters
    public String getParentID() {
        return parentID;
    }

    public void getParentEmail(String parentID, OnSuccessListener<String> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(parentID);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("emailUsername");
                        listener.onSuccess(email);
                    } else {
                        listener.onSuccess(null); // No user found
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error retrieving user document", e);
                    listener.onSuccess(null); // return null if error
                });
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    // Setter is omitted as parentID shouldn't change after creation.
    // For reference, LocalDate.of(int year, int month, int day) may be used for changing if needed

}
