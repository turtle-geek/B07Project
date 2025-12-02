package com.example.myapplication.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;
import android.os.CountDownTimer;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

// Renamed class for clarity, using the general function
public class TriageDecisionCritical extends AppCompatActivity {

    TextView liabilityWarning, liabilityWarning2, textView, textView2, textView3, textView4;
    ImageView yellow_card, red_card;
    Button cancel_button, yes_button, sos_button;
    ProgressBar progressBar;
    ConstraintLayout NonSos, SOS;

    private CountDownTimer call911Timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Assuming your layout file is named activity_triage_decision_card.xml
        setContentView(R.layout.activity_triage_critical);

        bindViews();

        String decision = getIntent().getStringExtra("DECISION");

        if ("SOS".equals(decision)) {
            SOSDecision();
        } else if ("NOT SOS".equals(decision)) {
            nonSOSDecision();
        } else{
            throw new RuntimeException("No decision made");
        }

        setListeners();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void bindViews(){
        liabilityWarning = findViewById(R.id.liabilityWarning);

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);

        red_card = findViewById(R.id.red_card);

        cancel_button = findViewById(R.id.cancel_button);

        progressBar = findViewById(R.id.progressBar);

        NonSos = findViewById(R.id.NonSOS);
        SOS = findViewById(R.id.SOS);
    }

    void setListeners(){
        sos_button.setOnClickListener(v -> {
            // IMMEDIATE CALL 911 (If your layout has an explicit SOS Call Button)
            // TODO: Implement actual phone call function here
            if (call911Timer != null) {
                call911Timer.cancel();
            }
        });

        cancel_button.setOnClickListener(v -> {
            if (call911Timer != null) {
                call911Timer.cancel();
            }

            nonSOSDecision();
        });

        yes_button.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeStepsRecovery.class));
            finish();
        });
    }

    void SOSDecision(){
        SOS.setVisibility(View.VISIBLE);
        NonSos.setVisibility(View.GONE);
        progressBar.setMax(100);

        call911Timer = new CountDownTimer(10000, 100) {

            @Override
            public void onFinish() {
                progressBar.setProgress(100);
                progressBar.setVisibility(View.GONE);
                cancel_button.setVisibility(View.GONE);
                textView2.setText("CALLING 911...");
                // TODO: Implement actual phone call function here
            }

            @Override
            public void onTick(long millisUntilFinished) {
                int totalDuration = 10000;
                int progressPercentage = (int) (100 * (totalDuration - millisUntilFinished) / totalDuration);
                int secondsLeft = (int) Math.ceil(millisUntilFinished / 1000.0);

                progressBar.setProgress(progressPercentage);

                String text = "CALLING 911 IN " + secondsLeft + " SECONDS";
                textView2.setText(text);
            }
        }.start();
    }

    void nonSOSDecision(){
        SOS.setVisibility(View.GONE);
        NonSos.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        if (call911Timer != null) {
            call911Timer.cancel();
        }
        super.onDestroy();
    }
}