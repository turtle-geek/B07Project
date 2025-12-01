package com.example.myapplication;

import android.content.Intent;
import android.widget.Toast;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.auth.LoginPage;
import com.example.myapplication.ui.ChildHomeActivity;
import com.example.myapplication.ui.ParentHomeActivity;
import com.example.myapplication.ui.ProviderHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Check if user is logged in
        currentUser = fAuth.getCurrentUser();

        if (currentUser == null) {
            // No user logged in, redirect to LoginPage
            Intent intent = new Intent(this, LoginPage.class);
            startActivity(intent);
            finish();
        } else {
            // User is logged in, check their role and redirect to appropriate page
            checkRole();
        }
    }
    private void checkRole(){
        //if user does not exist
        if(currentUser == null){
            Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        // use users' id to find their own documents
        // if document exists, find users' role on it, then depending on users' role land them on specific home page
        //If system cannot operate, show failure messages
        DocumentReference userDoc = fStore.collection("users").document(userId);
                userDoc.get().addOnSuccessListener(documentInfo -> {
                    if(documentInfo.exists()){
                        String role = documentInfo.getString("role");
                        if (role != null)
                            landonSpecificPage(role);
                        else{
                            Toast.makeText(this, "Cannot find user role", Toast.LENGTH_SHORT).show();
                            // Handle error internally:
                        }
                    }
                    else{
                        Toast.makeText(this, "Cannot find user information", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(this, "Cannot process: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
        }
        //helper function: depends on users' role, land user on their specific page
        private void landonSpecificPage(String role){
            Intent intent;
            switch (role) {
                case "child":
                    intent = new Intent(this, ChildHomeActivity.class);
                    intent.putExtra("id", currentUser.getUid());
                    break;
                case "parent":
                    intent = new Intent(this, ParentHomeActivity.class);
                    intent.putExtra("id", currentUser.getUid());
                    break;
                case "provider":
                    intent = new Intent(this, ProviderHomeActivity.class);
                    intent.putExtra("id", currentUser.getUid());
                    break;
                default:
                    Toast.makeText(this, "Unknown Character", Toast.LENGTH_SHORT).show();
                    return;
            }
            startActivity(intent);
            finish();
        }
}