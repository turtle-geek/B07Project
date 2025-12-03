package com.example.myapplication.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.auth.LogInModule.LogInViewActivity;
import com.example.myapplication.sosButtonResponse;
import com.example.myapplication.ui.ChildUI.ChildHomeActivity;
import com.example.myapplication.ui.ChildUI.ChildManagement;
import com.example.myapplication.ui.ChildUI.TriageAndResponse.HomeStepsRecovery;
import com.example.myapplication.ui.Inventory.InventoryLog;
// FIX: Correctly import the standalone Activity from the 'ui' package
import com.example.myapplication.ui.HistoryFilterActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SignOut_child extends AppCompatActivity {

    private static final String TAG = "SignOut_child";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private CardView medicationHistoryCard, inviteCard, reportCard;
    private Button logoutButton;
    private ImageButton profilePicture, sosButton;
    private TextView userNameText, userEmailText;
    private BottomNavigationView bottomNavigationView;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String currentUserId;

    // REMOVED: class HistoryFilterActivity extends AppCompatActivity {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signout_page_child);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        medicationHistoryCard = findViewById(R.id.medicationHistoryCard);
        inviteCard = findViewById(R.id.inviteCard);
        reportCard = findViewById(R.id.reportCard);
        logoutButton = findViewById(R.id.LogoutButton);
        profilePicture = findViewById(R.id.pfp_logo);
        userNameText = findViewById(R.id.userNameText);
        userEmailText = findViewById(R.id.userEmailText);
        bottomNavigationView = findViewById(R.id.menuBar);
        sosButton = findViewById(R.id.sosButton);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            loadUserInfo();
            loadProfilePicture();
        }

        setupImagePicker();

        if (bottomNavigationView != null) {
            setupBottomNavigation();
        }

        if (profilePicture != null) {
            profilePicture.setOnClickListener(v -> openImagePicker());
        }

        if (medicationHistoryCard != null) {
            medicationHistoryCard.setOnClickListener(v -> {
                Intent intent = new Intent(SignOut_child.this, InventoryLog.class);
                intent.putExtra("childId", currentUserId);
                startActivity(intent);
            });
        }

        sosButton.setOnClickListener(v -> {
            sosButtonResponse action = new sosButtonResponse();
            action.response(currentUserId, this);
        });


        if (inviteCard != null) {
            inviteCard.setOnClickListener(v -> {
                Toast.makeText(this, "Opening Invite", Toast.LENGTH_SHORT).show();
            });
        }

        // FIX: This listener now correctly references the external HistoryFilterActivity class
        if (reportCard != null) {
            reportCard.setOnClickListener(v -> navigateToHistoryFilter());
        }

        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> manualLogout());
        }
    }

    private void navigateToHistoryFilter() {
        Intent intent = new Intent(this, HistoryFilterActivity.class);

        if (currentUserId != null) {
            intent.putExtra("userId", currentUserId);
        }

        startActivity(intent);
        Toast.makeText(this, "Redirecting to History Filter", Toast.LENGTH_SHORT).show();
    }


    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            uploadProfilePicture(imageUri);
                        }
                    }
                });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadProfilePicture(Uri imageUri) {
        try {
            Toast.makeText(this, "Uploading profile picture...", Toast.LENGTH_SHORT).show();

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] data = baos.toByteArray();

            StorageReference profilePicRef = storage.getReference()
                    .child("profile_pictures")
                    .child(currentUserId + ".jpg");

            profilePicRef.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();

                            db.collection("users").document(currentUserId)
                                    .update("profilePictureUrl", downloadUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show();

                                        profilePicture.setImageBitmap(bitmap);
                                        profilePicture.setBackgroundResource(0);

                                        Log.d(TAG, "Profile picture uploaded successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to save picture URL", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Error saving picture URL", e);
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error uploading picture", e);
                    });

        } catch (IOException e) {
            Toast.makeText(this, "Error reading image", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error reading image", e);
        }
    }

    private void loadUserInfo() {
        if (currentUserId == null) return;

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");

                        if (name != null && userNameText != null) {
                            userNameText.setText(name);
                        }

                        if (email != null && userEmailText != null) {
                            userEmailText.setText(email);
                        }

                        Log.d(TAG, "User info loaded: " + name);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading user info", e);
                    if (userNameText != null) {
                        userNameText.setText("Error loading name");
                    }
                });
    }

    private void loadProfilePicture() {
        if (currentUserId == null) return;

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profilePicUrl = documentSnapshot.getString("profilePictureUrl");

                        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {

                            StorageReference profilePicRef = storage.getReferenceFromUrl(profilePicUrl);

                            profilePicRef.getBytes(1024 * 1024)
                                    .addOnSuccessListener(bytes -> {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        profilePicture.setImageBitmap(bitmap);
                                        profilePicture.setBackgroundResource(0);
                                        Log.d(TAG, "Profile picture loaded");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error downloading profile picture", e);
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading profile picture URL", e);
                });
    }

    private void setupBottomNavigation() {
        try {
            bottomNavigationView.setSelectedItemId(R.id.moreButton);

            bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();

                    if (id == R.id.homeButton) {
                        startActivity(new Intent(SignOut_child.this, ChildHomeActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    } else if (id == R.id.fileButton) {
                        startActivity(new Intent(SignOut_child.this, ChildManagement.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    } else if (id == R.id.nav_profile) {
                        startActivity(new Intent(SignOut_child.this, HomeStepsRecovery.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    } else if (id == R.id.moreButton) {
                        return true;
                    }

                    return false;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up navigation", e);
        }
    }

    private void manualLogout() {
        try {
            mAuth.signOut();

            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LogInViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Logout error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Logout error", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onStart", e);
        }
    }
}