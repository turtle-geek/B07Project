package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.auth.SignOut;
import com.example.myapplication.models.Child;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Child Management Activity
 * Uses the SAME layout template as ParentManagement
 * BUT:
 * - NO "Add Child" button (removed from layout)
 * - NO "Set Personal Best" card (removed from layout)
 * - Shows ONLY child's own profile (read-only)
 * - Uses SAME card layout as parent (activity_parent_childcard.xml)
 */
public class ChildManagement extends AppCompatActivity {

    private static final String TAG = "ChildManagement";

    private TextView tvEmptyState;
    private LinearLayout childrenCardsContainer;
    private Child currentChild;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentChildId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Uses child management layout (based on parent template)
        setContentView(R.layout.activity_child_management);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews();

        // Set up bottom navigation
        if (bottomNavigationView != null) {
            setupBottomNavigation();
        }

        // ✅ NO "Add Child" button - removed from layout
        // ✅ NO "Set Personal Best" card - removed from layout
        // ✅ NO "Share by Provider" filter - removed from layout
        // ✅ NO Doctor tags - removed from layout

        // Load current child's profile
        loadCurrentChildProfile();
    }

    private void initializeViews() {
        tvEmptyState = findViewById(R.id.tvEmptyState);
        childrenCardsContainer = findViewById(R.id.childrenCardsContainer);
        bottomNavigationView = findViewById(R.id.menuBar);

        // ❌ NO doctorFilterSwitch - removed from layout
        // ❌ NO doctorChipGroup - removed from layout
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload profile when returning to this activity
        loadCurrentChildProfile();
    }

    private void setupBottomNavigation() {
        try {
            // Set the current item as selected
            bottomNavigationView.setSelectedItemId(R.id.fileButton);

            bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();

                    if (id == R.id.homeButton) {
                        // Navigate to Child Home
                        startActivity(new Intent(ChildManagement.this, ChildHomeActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    } else if (id == R.id.fileButton) {
                        // Already on Child Management - do nothing
                        return true;

                    } else if (id == R.id.nav_profile) {
                        // Navigate to Child Tutorial
                        startActivity(new Intent(ChildManagement.this, HomeStepsRecovery.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    } else if (id == R.id.moreButton) {
                        // Navigate to Sign Out Page
                        startActivity(new Intent(ChildManagement.this, SignOut.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    }

                    return false;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up navigation", e);
        }
    }

    /**
     * ✅ Load current child's own profile
     * Shows only ONE card - the child's own profile
     */
    private void loadCurrentChildProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        currentChildId = currentUser.getUid();

        // Query the current child's document
        db.collection("users")
                .document(currentChildId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Child profile exists - created by parent
                        String childId = documentSnapshot.getId();
                        String childName = documentSnapshot.getString("name");
                        String parentId = documentSnapshot.getString("parentID");
                        String username = documentSnapshot.getString("emailUsername");
                        String email = documentSnapshot.getString("email");
                        String dob = documentSnapshot.getString("dateOfBirth");
                        String notes = documentSnapshot.getString("notes");
                        String role = documentSnapshot.getString("role");

                        // Create Child object
                        currentChild = new Child(childId, parentId, childName, email, role);
                        currentChild.setDOB(dob);
                        currentChild.setNotes(notes);

                        // Display the profile
                        updateUI(true);
                        displayChildProfile();

                        Log.d(TAG, "Loaded child profile: " + childName);
                    } else {
                        // Profile doesn't exist
                        Log.e(TAG, "Child profile not found in database");
                        updateUI(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading child profile", e);
                    Toast.makeText(this, "Failed to load profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    updateUI(false);
                });
    }

    private void updateUI(boolean hasProfile) {
        if (tvEmptyState != null && childrenCardsContainer != null) {
            if (!hasProfile) {
                tvEmptyState.setText("Profile not found. Please contact your parent.");
                tvEmptyState.setVisibility(View.VISIBLE);
                childrenCardsContainer.setVisibility(View.GONE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
                childrenCardsContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * ✅ Display child's profile using SAME card layout as parent
     * Uses: activity_parent_childcard.xml
     * BUT: Card is READ-ONLY (no click action)
     */
    private void displayChildProfile() {
        if (childrenCardsContainer == null || currentChild == null) return;

        childrenCardsContainer.removeAllViews();

        try {
            // ✅ Use the SAME card layout as parent
            View cardView = LayoutInflater.from(this).inflate(
                    R.layout.activity_parent_childcard,  // ← Same card as parent!
                    childrenCardsContainer,
                    false);

            TextView tvChildName = cardView.findViewById(R.id.tvChildName);
            TextView tvBirthday = cardView.findViewById(R.id.tvBirthday);
            TextView tvSpecialNote = cardView.findViewById(R.id.tvSpecialNote);
            CardView childCard = cardView.findViewById(R.id.childCard);

            // Set child's name
            if (tvChildName != null) {
                tvChildName.setText(currentChild.getName());
            }

            // Set birthday
            if (tvBirthday != null) {
                if (currentChild.getDateOfBirth() != null && !currentChild.getDateOfBirth().isEmpty()) {
                    tvBirthday.setText("Birthday: " + currentChild.getDateOfBirth());
                } else {
                    tvBirthday.setText("Birthday: Not provided");
                }
            }

            // Set special note
            if (tvSpecialNote != null) {
                if (currentChild.getNotes() != null && !currentChild.getNotes().isEmpty()) {
                    tvSpecialNote.setText("Special Note: " + currentChild.getNotes());
                } else {
                    tvSpecialNote.setText("Special Note: None");
                }
            }

            // Parent can click to edit, child cannot
            if (childCard != null) {
                childCard.setClickable(false);
                childCard.setFocusable(false);
                // Optional: Make card look less clickable
                childCard.setCardElevation(2);
                cardView.setAlpha(0.9f);
            }

            // Add the card to container
            childrenCardsContainer.addView(cardView);

        } catch (Exception e) {
            Log.e(TAG, "Error displaying child profile card", e);
            Toast.makeText(this, "Error displaying profile", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User not logged in, redirect to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}