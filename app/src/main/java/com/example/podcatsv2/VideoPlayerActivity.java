package com.example.podcatsv2;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.*;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO_URL = "VIDEO_URL"; // Constant key

    private static final String TAG = "VideoPlayerActivity";
    private PlayerView playerView;
    private ExoPlayer player;
    private String videoUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_player);

        playerView = findViewById(R.id.playerView);

        // Retrieve video URL from Intent using the constant key
        videoUrl = getIntent().getStringExtra(EXTRA_VIDEO_URL);

        if (videoUrl == null || videoUrl.isEmpty()) {
            Toast.makeText(this, "Invalid video URL", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Video URL is null or empty.");
            finish();
            return;
        }

        Log.d(TAG, "Received video URL: " + videoUrl);
    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        Uri uri = Uri.parse(videoUrl);
        MediaItem mediaItem = MediaItem.fromUri(uri);
        player.setMediaItem(mediaItem);

        // Add listener for logging
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                switch (state) {
                    case ExoPlayer.STATE_IDLE:
                        Log.d(TAG, "Player state: STATE_IDLE");
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        Log.d(TAG, "Player state: STATE_BUFFERING");
                        break;
                    case ExoPlayer.STATE_READY:
                        Log.d(TAG, "Player state: STATE_READY");
                        break;
                    case ExoPlayer.STATE_ENDED:
                        Log.d(TAG, "Player state: STATE_ENDED");
                        break;
                    default:
                        Log.d(TAG, "Player state: Unknown");
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e(TAG, "Playback error: " + error.getMessage(), error);
                Toast.makeText(VideoPlayerActivity.this, "Error playing video", Toast.LENGTH_SHORT).show();
            }
        });

        player.prepare();
        player.play();
        Log.d(TAG, "Player prepared and started.");
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
            Log.d(TAG, "Player released.");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }
}
