package com.example.myapplication.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import jp.wasabeef.blurry.Blurry;

public class ParentHomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parent_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homePage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();

        // Apply blur effects properly with .onto() - wrapped in Handler to prevent UI freeze
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                ViewGroup card1 = findViewById(R.id.statusCard1);
                Blurry.with(this)
                        .radius(25)
                        .sampling(2)
                        .color(Color.argb(66, 255, 255, 255))
                        .async()
                        .onto(card1);

                ViewGroup card2 = findViewById(R.id.statusCard2);
                Blurry.with(this)
                        .radius(25)
                        .sampling(2)
                        .color(Color.argb(66, 255, 255, 255))
                        .async()
                        .onto(card2);

                ViewGroup card3 = findViewById(R.id.statusCard3);
                Blurry.with(this)
                        .radius(25)
                        .sampling(2)
                        .color(Color.argb(66, 255, 255, 255))
                        .async()
                        .onto(card3);

                ViewGroup graphcard = findViewById(R.id.graphCard);
                Blurry.with(this)
                        .radius(25)
                        .sampling(2)
                        .color(Color.argb(66, 255, 255, 255))
                        .async()
                        .onto(graphcard);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 200);

    }
    //This method is to block users' access to visit this app's Parent Home Activities,
    // if they don't have an account
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            //people cannot log in
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

