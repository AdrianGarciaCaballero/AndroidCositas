package com.example.podcatsv2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";private static final int SPLASH_DURATION = 3000; // 3 seconds delay
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static boolean isAmplifyInitialized = false;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);

            mAuth = FirebaseAuth.getInstance();

            // Load image with Glide
            ImageView circleImage = findViewById(R.id.circleImage);
            Glide.with(this)
                    .load(R.drawable.logo) // Replace with your image resource or URL
                    .circleCrop()
                    .into(circleImage);
            //play asound if image cat is clicked
            circleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MeowPlayer.playCatSound(SplashActivity.this);  // Play sound on button click
                }
            });
            // Initialize ProgressBar
            progressBar = findViewById(R.id.progressBar);

            // Update ProgressBar and launch next activity after 30 seconds
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (progressStatus < 30) {
                        progressStatus++;
                        handler.post(() -> progressBar.setProgress(progressStatus));
                        try {
                            Thread.sleep(100); // Wait 1 second to increment progress
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // Launch next activity after 30 seconds
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }).start();


//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                        if (mAuth.getCurrentUser() != null) {
//                            // User is logged in
//                            startActivity(new Intent(SplashActivity.this, VideoFeedActivity.class));
//                        } else {
//                            // User is not logged in
//                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                        }
//
//                    finish();
//                }
//            }, SPLASH_DURATION);
//        }

        }

    }