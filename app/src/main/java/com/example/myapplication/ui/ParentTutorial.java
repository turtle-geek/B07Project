package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.example.myapplication.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class ParentTutorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_tutorial);

        // Initialize Rescue Inhaler YouTube Player
        YouTubePlayerView rescuePlayer = findViewById(R.id.rescueYoutubePlayer);
        getLifecycle().addObserver(rescuePlayer);

        rescuePlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "KGcaoDM5rds";
                youTubePlayer.cueVideo(videoId, 0);
            }
        });

        // Initialize Controller Inhaler YouTube Player
        YouTubePlayerView controllerPlayer = findViewById(R.id.controllerYoutubePlayer);
        getLifecycle().addObserver(controllerPlayer);

        controllerPlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "SsbbDOR-HU8";
                youTubePlayer.cueVideo(videoId, 0);
            }
        });
    }
}