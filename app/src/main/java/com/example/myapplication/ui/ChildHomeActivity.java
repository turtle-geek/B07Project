package com.example.myapplication.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.ImageButton;

import com.example.myapplication.CheckupNotificationReceiver;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;

import jp.wasabeef.blurry.Blurry;

public class ChildHomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ImageButton sosButton;

    private View prepostCheckPopup;
    private Child child;

    private String medicineLabel;
    private String medicineName;
    private double dosage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_child_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homePage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();

        setListeners();
    }

    private void setListeners() {
        sosButton = findViewById(R.id.sosButton);
        sosButton.setOnClickListener(v ->{
                startActivity(new Intent(this, TriageActivity.class));
                scheduleCheckupNotification();
        });
        // TODO: Load child from Firebase

        // Get medicine details from intent
        medicineLabel = getIntent().getStringExtra("medicineLabel");
        medicineName = getIntent().getStringExtra("medicineName");
        dosage = getIntent().getDoubleExtra("dosage", 0);

        // Initialize prepost check popup
        prepostCheckPopup = findViewById(R.id.prepostCheckPopup);

        // Check if we should show the prepost check
        if (getIntent().getBooleanExtra("SHOW_PREPOST_CHECK", false)) {
            showPrePostCheck();
        }

        // Set up button listeners for the prepost check
        setupPrePostCheckButtons();
    }

    private void showPrePostCheck() {
        if (prepostCheckPopup != null) {
            prepostCheckPopup.setVisibility(View.VISIBLE);
        }
    }

    private void hidePrePostCheck() {
        if (prepostCheckPopup != null) {
            prepostCheckPopup.setVisibility(View.GONE);
        }
    }

    private void setupPrePostCheckButtons() {
        ImageButton betterBtn = findViewById(R.id.better);
        ImageButton sameBtn = findViewById(R.id.same);
        ImageButton worseBtn = findViewById(R.id.Worse);

        if (betterBtn != null) {
            betterBtn.setOnClickListener(v -> {
                saveNewLogWithRating("Better");
                hidePrePostCheck();
            });
        }

        if (sameBtn != null) {
            sameBtn.setOnClickListener(v -> {
                saveNewLogWithRating("Same");
                hidePrePostCheck();
            });
        }

        if (worseBtn != null) {
            worseBtn.setOnClickListener(v -> {
                saveNewLogWithRating("Worse");
                hidePrePostCheck();
            });
        }
    }


    private void scheduleCheckupNotification() {
        long triggerTime = System.currentTimeMillis() + 10*60*1000; // 10 minutes
        Intent intent = new Intent(this, CheckupNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }

    private void saveNewLogWithRating(String rating) {
        if (child == null) {
            Toast.makeText(this, "Error: Child data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the medicine label
        MedicineLabel label = MedicineLabel.valueOf(medicineLabel);

        // Get the InventoryItem from the child's inventory
        InventoryItem medicineItem = child.getInventory().getMedicine(label);

        // Create a NEW log entry with the rating
        LocalDateTime timestamp = LocalDateTime.now();
        TechniqueQuality quality = (label == MedicineLabel.CONTROLLER) ? TechniqueQuality.HIGH : TechniqueQuality.NA;

        MedicineUsageLog newLog = new MedicineUsageLog(
                medicineItem,
                dosage,
                timestamp,
                quality,
                rating  // Include the rating
        );

        // Add the new log to the appropriate list (Controller or Rescue)
        if (label == MedicineLabel.CONTROLLER) {
            child.getInventory().getControllerLog().add(newLog);
        } else {
            child.getInventory().getRescueLog().add(newLog);
        }

        // TODO: Save updated child data to Firebase

        Toast.makeText(this, "Rating saved: " + rating, Toast.LENGTH_SHORT).show();
    }

    //This method is to block users' access to visit this app's Child Home Activities,
    // if they don't have an account
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}