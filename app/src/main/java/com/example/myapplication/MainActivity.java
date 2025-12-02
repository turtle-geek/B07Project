package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.auth.AuthManager;
import com.example.myapplication.auth.SessionManager;
import com.example.myapplication.auth.LoginPage;
import com.example.myapplication.ui.ChildHomeActivity;
import com.example.myapplication.ui.Onboarding;
import com.example.myapplication.ui.ParentHomeActivity;
import com.example.myapplication.ui.ProviderHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AuthManager.signOut();

        // Remove firebase data persistence
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(
                        MemoryCacheSettings.newBuilder().build()
                )
                .build();

        db.setFirestoreSettings(settings);

        fAuth = FirebaseAuth.getInstance();

        checkAndRouteUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AuthManager.attachAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AuthManager.detachAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        checkAndRouteUser();
    }

    private void checkAndRouteUser() {
        FirebaseUser currentUser = fAuth.getCurrentUser();

        if (currentUser == null) {
            if (!isFinishing()) {
                Intent intent = new Intent(this, LoginPage.class);
                startActivity(intent);
                finish();
            }
            return;
        }

        if (SessionManager.getInstance().hasUser()) {
            checkRoleAndOnboarding(currentUser.getUid());
        } else {
            checkRoleAndOnboarding(currentUser.getUid());
        }
    }

    private void checkRoleAndOnboarding(String userId) {
        FirebaseFirestore.getInstance().collection("users").document(userId).get()
                .addOnSuccessListener(documentInfo -> {
                    if(documentInfo.exists()){
                        Boolean onboardingCompleted = documentInfo.getBoolean("onboardingCompleted");

                        if (onboardingCompleted == null || !onboardingCompleted) {
                            Intent intent = new Intent(this, Onboarding.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String role = documentInfo.getString("role");
                            if (role != null) {
                                landonSpecificPage(role);
                            } else {
                                Toast.makeText(this, "Cannot find user role", Toast.LENGTH_SHORT).show();
                                AuthManager.signOut();
                            }
                        }
                    }
                    else{
                        Toast.makeText(this, "Cannot find user information, starting onboarding.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, Onboarding.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(this, "Cannot process Firestore data: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    AuthManager.signOut();
                });
    }

    private void landonSpecificPage(String role) {
        Intent intent;
        String userId = fAuth.getCurrentUser().getUid();

        switch (role) {
            case "Child":
            case "child":
                intent = new Intent(this, ChildHomeActivity.class);
                intent.putExtra("id", userId);
                break;
            case "Parent":
            case "parent":
                intent = new Intent(this, ParentHomeActivity.class);
                intent.putExtra("id", userId);
                break;
            case "Provider":
            case "provider":
                intent = new Intent(this, ProviderHomeActivity.class);
                intent.putExtra("id", userId);
                break;
            default:
                Toast.makeText(this, "Unknown Character Role: " + role, Toast.LENGTH_SHORT).show();
                AuthManager.signOut();
                return;
        }
        startActivity(intent);
        finish();
    }
}