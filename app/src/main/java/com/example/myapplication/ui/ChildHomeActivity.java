package com.example.myapplication.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.CheckupNotificationReceiver;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;



public class ChildHomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Initialize Firestore instance
    ImageButton sosButton;

    CardView dailyCheckInCard;

    // --- Time range for today's check-in ---
    private long startOfDayTimestamp;
    private long endOfDayTimestamp;
    // ---------------------------------------

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
        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        // Calculate the start and end of the current day in milliseconds
        calculateDayTimestamps();

        setListeners();
    }

    /**
     * Calculates the start (00:00:00) and end (23:59:59) timestamps for the current day.
     */
    private void calculateDayTimestamps() {
        Calendar calendar = Calendar.getInstance();

        // Set end date to the very end of today (23:59:59)
        calendar.setTime(new Date()); // Ensure we start with the current time
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        endOfDayTimestamp = calendar.getTimeInMillis();

        // Set start date to the very start of today (00:00:00)
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        startOfDayTimestamp = calendar.getTimeInMillis();
    }

    private void setListeners() {
        sosButton = findViewById(R.id.sosButton);
        sosButton.setOnClickListener(v ->{
            startActivity(new Intent(this, TriageActivity.class));
            scheduleCheckupNotification();
        });

        dailyCheckInCard = findViewById(R.id.graphCard1);
        // Change the listener to check for duplicates before starting the activity
        dailyCheckInCard.setOnClickListener(v -> checkForExistingCheckIn());
    }

    /**
     * Queries Firestore to check if a DailyCheckIn has already been submitted today.
     */
    private void checkForExistingCheckIn() {
        // DailyCheckInActivity.java saves check-ins to the "daily_checkins" collection.
        // It uses a 'timestamp' field. We query documents where the timestamp is within today's range.

        db.collection("daily_checkins")
                .whereGreaterThanOrEqualTo("checkInTimestamp", startOfDayTimestamp)
                .whereLessThanOrEqualTo("checkInTimestamp", endOfDayTimestamp)
                .limit(1) // We only need to find one document to confirm a duplicate
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            // A check-in for today exists
                            Toast.makeText(this, "Daily Check-in for today already completed.", Toast.LENGTH_LONG).show();
                        } else {
                            // No check-in for today, proceed to the activity
                            startActivity(new Intent(this, DailyCheckInActivity.class));
                        }
                    } else {
                        Log.e("ChildHomeActivity", "Error checking for existing check-in: " + task.getException());
                        // As a fallback, allow the user to proceed if the check fails, to avoid blocking them.
                        Toast.makeText(this, "Could not verify today's entry status. Proceeding...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, DailyCheckInActivity.class));
                    }
                });
    }

    private void scheduleCheckupNotification() {
        long triggerTime = System.currentTimeMillis() + 10*60*1000;
        Intent intent = new Intent(this, CheckupNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            Toast.makeText(this, "Notification scheduled for 10 minutes.", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Log.e("ChildHomeActivity", "Failed to schedule exact alarm: Missing SCHEDULE_EXACT_ALARM permission.", e);
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            Toast.makeText(this, "Notification scheduled (inexact time).", Toast.LENGTH_SHORT).show();
        }
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