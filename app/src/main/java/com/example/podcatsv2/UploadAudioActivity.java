package com.example.podcatsv2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UploadAudioActivity extends AppCompatActivity {

    private static final String TAG = "UploadAudioActivity";
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int REQUEST_THUMBNAIL_PICK = 101;

    private Button buttonRecordAudio, buttonUpload, buttonSelectThumbnail;
    private EditText editTextTitle, editTextDescription;
    private ImageView imageViewThumbnail;
    private ProgressBar progressBarUpload;

    private Uri audioUri;
    private String audioFilePath;
    private Uri thumbnailUri;

    private FirebaseAuth mAuth;
    private DatabaseReference audiosRef;
    private AWSManager awsManager;

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_audio);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        audiosRef = FirebaseDatabase.getInstance().getReference("Audios");

        // Initialize AWSManager
        awsManager = new AWSManager(this);

        // Initialize UI elements
        buttonRecordAudio = findViewById(R.id.buttonRecordAudio);
        buttonUpload = findViewById(R.id.buttonUpload);
        buttonSelectThumbnail = findViewById(R.id.buttonSelectThumbnail);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        imageViewThumbnail = findViewById(R.id.imageViewThumbnail);
        progressBarUpload = findViewById(R.id.progressBarUpload);

        // Set up listeners
        buttonRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRecordAudio();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  uploadAudio();
            }
        });

        buttonSelectThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectThumbnailFromGallery();
            }
        });
    }


    private void handleRecordAudio() {
        if (!isRecording) {
            // Start recording
            checkAndRequestPermissionsForRecording();
        } else {
            // Stop recording
            stopRecordingAudio();
        }
    }


    private void checkAndRequestPermissionsForRecording() {
        List<String> listPermissionsNeeded = new ArrayList<>();

        // Always check for RECORD_AUDIO permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
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
            startRecordingAudio();
        }
    }


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
                startRecordingAudio();
            } else {
                // Some permissions are denied
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.permissions_required_title)
                        .setMessage(R.string.permissions_required_message)
                        .setPositiveButton(R.string.grant, (dialog, which) -> checkAndRequestPermissionsForRecording())
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        }
    }


    private void startRecordingAudio() {
        // Create a file in the external files directory to store the audio
        File audioFile = new File(getExternalFilesDir(null), "audio_" + System.currentTimeMillis() + ".m4a");
        audioFilePath = audioFile.getAbsolutePath(); // Store the path for later use

        // Initialize MediaRecorder
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(audioFilePath); // Save the audio to a known path
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioEncodingBitRate(128000);
        mediaRecorder.setAudioSamplingRate(44100);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            Toast.makeText(this, getString(R.string.recording_started), Toast.LENGTH_SHORT).show();

            // Change button text to "Stop Recording"
            buttonRecordAudio.setText(R.string.stop_recording);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.recording_failed), Toast.LENGTH_SHORT).show();
            isRecording = false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.recording_failed), Toast.LENGTH_SHORT).show();
            isRecording = false;
        }
    }


    private void stopRecordingAudio() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            Toast.makeText(this, getString(R.string.recording_stopped), Toast.LENGTH_SHORT).show();

            // Reset the button to "Record Audio"
            buttonRecordAudio.setText(R.string.record_audio);

            // Set the audioUri to the recorded file
            audioUri = Uri.fromFile(new File(audioFilePath));

        } catch (RuntimeException stopException) {
            stopException.printStackTrace();
            Toast.makeText(this, getString(R.string.recording_failed), Toast.LENGTH_SHORT).show();
            // Delete the corrupted audio file
            File corruptedFile = new File(audioFilePath);
            if (corruptedFile.exists()) {
                corruptedFile.delete();
            }
            isRecording = false;
            buttonRecordAudio.setText(R.string.record_audio);
        }
    }


    private void selectThumbnailFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_THUMBNAIL_PICK);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_THUMBNAIL_PICK) {
                thumbnailUri = data.getData();
                if (thumbnailUri != null) {
                    imageViewThumbnail.setImageURI(thumbnailUri);
                    Toast.makeText(this, getString(R.string.thumbnail_selected), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



}
