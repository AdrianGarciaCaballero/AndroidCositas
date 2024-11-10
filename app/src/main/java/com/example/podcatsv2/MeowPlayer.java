package com.example.podcatsv2;

import android.content.Context;
import android.media.MediaPlayer;

public class MeowPlayer {
    private static MediaPlayer mediaPlayer;

    // Initialize the MediaPlayer with the cat sound
    public static void playCatSound(Context context) {
        // Release any previous instance of mediaPlayer if it's playing
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        // Create a new MediaPlayer with the cat sound
        mediaPlayer = MediaPlayer.create(context, R.raw.cat_meow);

        // Start playing the sound
        if (mediaPlayer != null) {
            mediaPlayer.start();

            // Release the MediaPlayer resources when done playing
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
            });
        }
    }
}