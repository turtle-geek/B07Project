package com.example.myapplication.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.IncidentLogEntry;
import com.example.myapplication.adapters.TriageHistoryAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TriageHistoryActivity extends AppCompatActivity {

    private String targetUsername; // Renamed to clarify: this is the child's username
    private String targetUserId;   // Added: The child's UID passed via Intent
    private RecyclerView recyclerView;
    private TextView tvLoading;
    private TriageHistoryAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private static final String TAG = "TriageHistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triage_history);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tvLoading = findViewById(R.id.tvLoading);
        recyclerView = findViewById(R.id.recyclerViewTriageHistory);
        setupBackButton();

        // 1. Get the target child's ID from the Intent
        targetUserId = getIntent().getStringExtra("childId");

        if (targetUserId == null) {
            // Fallback: If no child ID is passed, use the logged-in user's ID
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                targetUserId = currentUser.getUid();
                Log.i(TAG, "No childId passed. Defaulting to current user's ID.");
            } else {
                Toast.makeText(this, "Error: User not logged in and no child selected.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        checkAndSetupTargetUser(targetUserId);
    }

    private void setupBackButton() {
        ImageButton backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void checkAndSetupTargetUser(String uid) {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Fetch the target user's (child's) username
                        String fullEmailUsername = documentSnapshot.getString("emailUsername");

                        if (fullEmailUsername != null && !fullEmailUsername.isEmpty()) {
                            int atIndex = fullEmailUsername.indexOf('@');
                            String cleanUsername = (atIndex > 0) ? fullEmailUsername.substring(0, atIndex) : fullEmailUsername;

                            if (!cleanUsername.isEmpty()) {
                                this.targetUsername = cleanUsername;
                                fetchTriageHistory(this.targetUsername);
                            } else {
                                Toast.makeText(this, "Error: Target user's username not found.", Toast.LENGTH_LONG).show();
                                finishSetup(false);
                            }
                        } else {
                            finishSetup(false);
                        }
                    } else {
                        finishSetup(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch target user data: ", e);
                    finishSetup(false);
                });
    }

    private void fetchTriageHistory(String username) {
        if (username == null) {
            finishSetup(false);
            return;
        }

        db.collection("triage_incidents")
                .whereEqualTo("username", username) // Query using the child's username
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<IncidentLogEntry> entries = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                IncidentLogEntry entry = document.toObject(IncidentLogEntry.class);
                                entries.add(entry);

                            } catch (Exception e) {
                                Log.e(TAG, "Error deserializing entry: " + e.getMessage());
                            }
                        }
                        setupRecyclerView(entries);
                        finishSetup(!entries.isEmpty());
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                        finishSetup(false);
                    }
                });
    }

    private void setupRecyclerView(List<IncidentLogEntry> entries) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TriageHistoryAdapter(entries);
        recyclerView.setAdapter(adapter);
    }

    private void finishSetup(boolean dataFound) {
        tvLoading.setVisibility(View.GONE);
        if (dataFound) {
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            tvLoading.setText("No triage incidents found for this user.");
            tvLoading.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}