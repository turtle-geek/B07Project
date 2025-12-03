package com.example.myapplication.ui.ChildUI.TriageAndResponse;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
// Assuming HomeStepsRecovery is correctly imported here
import com.example.myapplication.ui.ChildUI.TriageAndResponse.HomeStepsRecovery;

public class TriageNonCriticalActivity extends AppCompatActivity {

    private static final String TAG = "TriageNonCritical";
    private static final int CALL_PERMISSION_REQUEST_CODE = 102; // Use a distinct request code

    // Views from the NonSOS card in the XML
    private Button btnCall911NonSOS;
    private Button btnStartRecovery;
    private ImageButton btnBack;

    private String userId; // Variable to store the user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triage_non_critical);

        // Retrieve the user ID passed from TriageActivity
        userId = getIntent().getStringExtra("id");
        if (userId == null) {
            // Log a warning if the ID is missing, though the app should handle this gracefully
            Log.w(TAG, "User ID is missing from Intent.");
        }

        bindViews();
        setListeners();
    }

    private void bindViews() {
        btnCall911NonSOS = findViewById(R.id.btn_call_911_non_sos);
        btnStartRecovery = findViewById(R.id.btn_start_recovery);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 1. Call 911 button (Manual call, no countdown)
        if (btnCall911NonSOS != null) {
            btnCall911NonSOS.setOnClickListener(v -> makeEmergencyCall());
        }

        // 2. Start Recovery button (Navigate to the HomeStepsRecovery screen)
        if (btnStartRecovery != null) {
            btnStartRecovery.setOnClickListener(v -> startRecoverySteps());
        }
    }

    private void makeEmergencyCall() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    CALL_PERMISSION_REQUEST_CODE);
        } else {
            initiateCall();
        }
    }

    private void initiateCall() {
        try {
            String phoneNumber = "911";
            // Use ACTION_DIAL to prompt the user before calling
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        } catch (SecurityException e) {
            Toast.makeText(this, "Call failed: Permission or security issue.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void startRecoverySteps() {
        // FIX: Navigate to HomeStepsRecovery.class
        Intent intent = new Intent(this, HomeStepsRecovery.class);

        // Pass the user ID so HomeStepsRecovery can detect the user's role
        if (userId != null) {
            intent.putExtra("id", userId);
        }

        startActivity(intent);
        // Finish TriageNonCriticalActivity as the user is now moving into recovery/guidance
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initiateCall();
            } else {
                Toast.makeText(this, "Permission denied. Manual dialing required.", Toast.LENGTH_LONG).show();
            }
        }
    }
}