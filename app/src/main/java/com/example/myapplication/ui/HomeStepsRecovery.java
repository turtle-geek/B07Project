package com.example.myapplication.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class HomeStepsRecovery extends AppCompatActivity {

    CheckBox step1, step2, step3, step4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ REMOVED EdgeToEdge.enable(this) - causes crash

        setContentView(R.layout.activity_home_steps_recovery);

        initializeBoxes();
        setUpVideos();

        findViewById(R.id.doneButton).setOnClickListener(v -> {
            if (checkBoxes()) {
                finish();
            }
        });

        // ✅ REMOVED ViewCompat.setOnApplyWindowInsetsListener - causes crash
        // The crash happens because findViewById(R.id.main) returns null
        // R.id.main doesn't exist in activity_home_steps_recovery.xml
    }

    void initializeBoxes(){
        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);
        step4 = findViewById(R.id.step4);

        step1.setChecked(false);
        step2.setChecked(false);
        step3.setChecked(false);
        step4.setChecked(false);
    }

    void setUpVideos(){
        // Initialize Rescue Inhaler YouTube Player
        YouTubePlayerView rescuePlayer = findViewById(R.id.rescueYoutubePlayer);
        getLifecycle().addObserver(rescuePlayer);

        rescuePlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "LU-pRbN7AD4";
                youTubePlayer.cueVideo(videoId, 60);
            }
        });

        // Initialize Breathing Exercises YouTube Player
        YouTubePlayerView breathingPlayer = findViewById(R.id.breathingPlayer);
        getLifecycle().addObserver(breathingPlayer);

        breathingPlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "FyjZLPmZ534";
                youTubePlayer.cueVideo(videoId, 0);
            }
        });
    }

    boolean checkBoxes(){
        // ✅ FIXED LOGIC: Was backwards before!
        // Should return true only when ALL boxes are checked
        if (step1.isChecked() && step2.isChecked() && step3.isChecked() && step4.isChecked()){
            // All steps completed
            return true;
        } else {
            // Not all steps completed - show message
            Toast.makeText(this, "Please complete all steps", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}