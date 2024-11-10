package com.example.podcatsv2;


import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UploadVideosActivity extends AppCompatActivity {
        private static final int REQUEST_VIDEO_CAPTURE = 1;
        private static final int REQUEST_PERMISSIONS = 100;

        private Button buttonRecordVideo, buttonUpload, btnnavupload;
        private EditText editTextTitle, editTextDescription;
        private ProgressBar progressBarUpload;

        private Uri videoUri;
        private Bitmap thumbnailBitmap;

        private FirebaseAuth mAuth;
        private DatabaseReference videosRef;
        private AWSManager awsManager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_upload_videos);

            // Initialize Firebase
            mAuth = FirebaseAuth.getInstance();
            videosRef = FirebaseDatabase.getInstance().getReference("Videos");

            // Initialize AWSManager
            awsManager = new AWSManager(this);

            // Initialize UI elements
            buttonRecordVideo = findViewById(R.id.buttonRecordVideo);
            editTextTitle = findViewById(R.id.editTextTitle);
            editTextDescription = findViewById(R.id.editTextDescription);
            progressBarUpload = findViewById(R.id.progressBarUpload);
            buttonUpload = findViewById(R.id.buttonUpload);
            //btnnavupload = findViewById(R.id.nav_upload);

            // Set up listeners
            buttonRecordVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkAndRequestPermissions();
                }
            });

            buttonUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadVideoAndThumbnail();
                }
            });

            // Initialize Bottom Navigation
            initializeBottomNavigation();
        }

    private void initializeBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_video) {
                // Already on Feed, do nothing
                return true;
            } else if (itemId == R.id.nav_feed) {
                // Navigate to UploadVideosActivity
                Intent uploadIntent = new Intent(UploadVideosActivity.this, VideoFeedActivity.class);
                startActivity(uploadIntent);
                return true;
            } else if (itemId == R.id.nav_settings) {
                // Navigate to SettingsActivity
                Intent settingsIntent = new Intent(UploadVideosActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            } else if (itemId == R.id.nav_audio) {
                // Navigate to SettingsActivity
                Intent audioIntent = new Intent(UploadVideosActivity.this, UploadAudioActivity.class);
                startActivity(audioIntent);
                return true;
            }

            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_video);

    }

        private void checkAndRequestPermissions() {
            List<String> listPermissionsNeeded = new ArrayList<>();

            // Always check for CAMERA permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }

            // Check for READ_MEDIA_VIDEO for Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API level 33
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_VIDEO);
                }
            } else { // Below Android 13
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }

            // Check for WRITE_EXTERNAL_STORAGE for Android below 10
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) { // API level 29
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }

            // If any permissions are not granted, request them
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), REQUEST_PERMISSIONS);
            } else {
                // All permissions are granted, proceed with recording
                recordVideo();
            }
        }

        // Handle the permission request response
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == REQUEST_PERMISSIONS) {
                boolean allGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }
                }

                if (allGranted) {
                    // All permissions are granted
                    recordVideo();
                } else {
                    // Some permissions are denied
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Permissions Required")
                            .setMessage("Camera and Storage permissions are required to record and upload videos.")
                            .setPositiveButton("Grant", (dialog, which) -> checkAndRequestPermissions())
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            }
        }

        private void recordVideo() {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30); // Limit video to 30 seconds
            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            } else {
                Toast.makeText(this, getString(R.string.no_camera_app), Toast.LENGTH_SHORT).show();
            }
        }

        private void uploadVideoAndThumbnail() {
            final String title = editTextTitle.getText().toString().trim();
            final String description = editTextDescription.getText().toString().trim();

            if (videoUri == null) {
                Toast.makeText(this, getString(R.string.record_video_first), Toast.LENGTH_SHORT).show();
                return;
            }

            if (title.isEmpty()) {
                editTextTitle.setError(getString(R.string.title_required));
                editTextTitle.requestFocus();
                return;
            }

            if (description.isEmpty()) {
                editTextDescription.setError(getString(R.string.description_required));
                editTextDescription.requestFocus();
                return;
            }

            progressBarUpload.setVisibility(View.VISIBLE);
            buttonUpload.setEnabled(false);

            // Upload video to AWS S3
            awsManager.uploadVideo(videoUri, new AWSManager.AWSCallback() {
                @Override
                public void onSuccess(String videoUrl) {
                    // Generate a unique name for the thumbnail
                    String thumbnailFileName = "thumb_" + UUID.randomUUID().toString() + ".jpg";
                    // Upload thumbnail to AWS S3
                    awsManager.uploadThumbnail(thumbnailBitmap, thumbnailFileName, new AWSManager.AWSCallback() {
                        @Override
                        public void onSuccess(String thumbnailUrl) {
                            // Save video metadata to Firebase Realtime Database
                            String videoId = videosRef.push().getKey();
                            if (videoId != null) {
                                Video video = new Video();
                                video.setVideoId(videoId);
                                video.setTitle(title);
                                video.setDescription(description);
                                video.setUrl(videoUrl);
                                video.setThumbnailUrl(thumbnailUrl);
                                video.setUserId(mAuth.getCurrentUser().getUid());
                                video.setPublicationTime(new Date().toString());
                                video.setLikeCount(0);

                                videosRef.child(videoId).setValue(video)
                                        .addOnCompleteListener(task -> {
                                            runOnUiThread(() -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(UploadVideosActivity.this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
                                                    resetUploadForm();
                                                } else {
                                                    Toast.makeText(UploadVideosActivity.this, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
                                                }

                                                //stop show progress bar
                                                progressBarUpload.setVisibility(View.GONE);
                                                buttonUpload.setEnabled(true);
                                            });
                                        });
                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(UploadVideosActivity.this, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
                                    progressBarUpload.setVisibility(View.GONE);
                                    buttonUpload.setEnabled(true);
                                });
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(UploadVideosActivity.this, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
                                progressBarUpload.setVisibility(View.GONE);
                                buttonUpload.setEnabled(true);
                            });
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(UploadVideosActivity.this, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
                        progressBarUpload.setVisibility(View.GONE);
                        buttonUpload.setEnabled(true);
                    });
                }
            });
        }

        private void resetUploadForm() {
            videoUri = null;
            thumbnailBitmap = null;
            editTextTitle.setText("");
            editTextDescription.setText("");
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
                videoUri = data.getData();
                try {
                    // Generate thumbnail
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(this, videoUri);
                    thumbnailBitmap = retriever.getFrameAtTime(1000000); // 1 second (in microseconds)
                    retriever.release();
                    Toast.makeText(this, getString(R.string.video_recorded), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, getString(R.string.thumbnail_error), Toast.LENGTH_SHORT).show();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
