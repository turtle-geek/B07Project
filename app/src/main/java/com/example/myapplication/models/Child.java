package com.example.myapplication.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Child extends User{

    String parentID;

    // Constructor to create child profile under a parent
    public Child(String id, String name, String parentID) {
        super(id, name);
        this.parentID = parentID;
        getParentEmail(parentID, email -> {
            if (email != null) {
                this.email = email;
            } else {
                Log.d("Firestore", "Parent not found");
            }
        });
    }

    // Overloaded constructor for child to create their own profile
    public Child(String id, String parentID, String name, String email){
        super(id, name);
        this.parentID = parentID; // might turn this into a setter instead but that wouldn't work with the overloading
        this.email = email;
    }
    // Public Setters
    public void setParentID(String parentID) {
        this.parentID = parentID;
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
                        String email = documentSnapshot.getString("email");
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

    // Setter is omitted as parentID shouldn't change after creation.
}
