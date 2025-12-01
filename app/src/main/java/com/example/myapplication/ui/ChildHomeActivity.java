package com.example.myapplication.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.CheckupNotificationReceiver;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.models.Child;
import com.example.myapplication.models.HealthProfile;
import com.example.myapplication.models.PeakFlow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import jp.wasabeef.blurry.Blurry;

public class ChildHomeActivity extends AppCompatActivity {
    ImageButton sosButton;
    Button pefButton;
    CardView pefCard;
    TextView pefDisplay, pefDateTime;
    ConstraintLayout editPEF;
    HealthProfile hp;
    Child currentChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_child_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homePage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        editPEF = findViewById(R.id.editPEF);
        pefCard = findViewById(R.id.pefCard);
        pefDisplay = findViewById(R.id.pefDisplay);
        pefButton = findViewById(R.id.pefButton);
        pefDateTime = findViewById(R.id.pefDateTime);

        editPEF.setVisibility(View.GONE);

        String id = getIntent().getStringExtra("id");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (id == null) {
            Log.e("ChildHomeActivity", "No child ID provided");
            return;
        } else {
            db.collection("users").document(id).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (!documentSnapshot.exists()) return;

                        currentChild = documentSnapshot.toObject(Child.class);
                        if (currentChild == null) return;

                        hp = currentChild.getHealthProfile();
                        if (hp == null || hp.getPEFLog() == null || hp.getPEFLog().isEmpty())
                            return;
                        displayTodayPeakFlow();
                    });
        }

        setListeners();
    }

    private void displayTodayPeakFlow() {
        ArrayList<PeakFlow> log = hp.getPEFLog();
        PeakFlow latest = log.get(log.size() - 1);

        updatePeakFlowUI(latest);
    }

    private void updatePeakFlowUI(PeakFlow todayPeakFlow) {
        pefDisplay.setText(String.valueOf(todayPeakFlow.getPeakFlow()));

        // Convert time format
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("h:mm a, MMM d");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pefDateTime.setText(todayPeakFlow.getTime().format(formatter));
        }

        switch (todayPeakFlow.getZone()) {
            case "green":
                pefCard.setCardBackgroundColor(Color.parseColor("#008000"));
                break;
            case "yellow":
                pefCard.setCardBackgroundColor(Color.parseColor("#FFD700"));
                break;
            case "red":
                pefCard.setCardBackgroundColor(Color.parseColor("#FF0000"));
                break;
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setListeners() {
        sosButton = findViewById(R.id.sosButton);
        sosButton.setOnClickListener(v ->{
                Intent intent = new Intent(this, TriageActivity.class);
                intent.putExtra("id", currentChild.getId());
                startActivity(intent);
                scheduleCheckupNotification();
        });

        pefButton.setOnClickListener(v -> {
            editPEF.setVisibility(View.VISIBLE);
            TextView editTextNumber = findViewById(R.id.editTextNumber);
            editTextNumber.setOnFocusChangeListener((view, hasFocus) -> {
                if (editTextNumber.hasFocus()) {
                    // Uh;
                } else {
                    String text = editTextNumber.getText().toString();
                    int peakFlowValue;
                    if (!text.isEmpty()) {
                        peakFlowValue = Integer.parseInt(text);
                        LocalDateTime submitTime = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            submitTime = LocalDateTime.now();
                        }

                        if (submitTime != null){
                            PeakFlow pef = new PeakFlow(peakFlowValue, submitTime);
                            pef.computeZone(currentChild);
                            hp.addPEFToLog(pef);
                        }
                    }
                }
            });
        });
    }

    @SuppressLint("ScheduleExactAlarm")
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private void scheduleCheckupNotification() {
        long triggerTime = System.currentTimeMillis() + 10*60*1000; // 10 minutes
        Intent intent = new Intent(this, CheckupNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }

    //This method is to block users' access to visit this app's Child Home Activities,
    // if they don't have an account
    @Override
    protected void onStart(){
        super.onStart();
        if(currentChild == null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}