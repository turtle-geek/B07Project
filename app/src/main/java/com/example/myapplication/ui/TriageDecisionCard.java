package com.example.myapplication.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

public class TriageDecisionCard extends AppCompatActivity {

    TextView liabilityWarning, liabilityWarning2, textView, textView2, textView3, textView4;
    ImageView yellow_card, red_card;
    Button cancel_button, yes_button, sos_button;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_triage_decision_card);

        bindViews();
        setListeners();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void bindViews(){
        liabilityWarning = findViewById(R.id.liabilityWarning);
        liabilityWarning2 = findViewById(R.id.liabilityWarning2);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        red_card = findViewById(R.id.red_card);
        yellow_card = findViewById(R.id.yellow_card);
        cancel_button = findViewById(R.id.cancel_button);
        yes_button = findViewById(R.id.yes_button);
        sos_button = findViewById(R.id.sos_button);
        progressBar = findViewById(R.id.progressBar);
    }

    void setListeners(){
        sos_button.setOnClickListener(v -> {

        });
    }

}