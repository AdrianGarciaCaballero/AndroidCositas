package com.example.podcatsapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View; // Add this import
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import com.example.podcatsapp.R;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editUsername;
    private ImageView profileImage;
    private SharedPreferences sharedPreferences;
    private static final int PICK_IMAGE_REQUEST = 1; // Code for image picker
    private static final String USERNAME_KEY = "username";
    private static final String PROFILE_IMAGE_URI_KEY = "profile_image_uri"; // For storing profile image URI
    private static final String DARK_MODE_KEY = "dark_mode_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // Set the theme before setting content view
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        updateTheme(isDarkMode);

        setContentView(R.layout.activity_edit_profile);

        // Set up the toolbar or back button functionality
       // Toolbar toolbar = findViewById(R.id.toolbar);
     //   setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable the back button
        }

        // Initialize views
        editUsername = findViewById(R.id.edit_username);
        profileImage = findViewById(R.id.profile_image);

        // Load saved username and profile image
        loadUserProfile();

        // Set listeners
        profileImage.setOnClickListener(v -> openImagePicker());
        findViewById(R.id.save_button).setOnClickListener(v -> saveChanges());
    }

    // Open an image picker to change the profile picture
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result from the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            profileImage.setImageURI(data.getData()); // Set the selected image
        }
    }

    // Load saved username and profile image from SharedPreferences
    private void loadUserProfile() {
        String username = sharedPreferences.getString(USERNAME_KEY, "Default Username");
        String profileImageUri = sharedPreferences.getString(PROFILE_IMAGE_URI_KEY, null);

        editUsername.setText(username);
        if (profileImageUri != null) {
            profileImage.setImageURI(android.net.Uri.parse(profileImageUri)); // Load profile image if available
        }
    }

    // Save the changes made to the profile
    private void saveChanges() {
        String username = editUsername.getText().toString().trim();

        // Validate the username
        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the username and profile image URI in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);

        // If the profile image URI is available, save it as well
        android.net.Uri imageUri = (android.net.Uri) profileImage.getTag(); // Assuming profile image URI is stored as a tag
        if (imageUri != null) {
            editor.putString(PROFILE_IMAGE_URI_KEY, imageUri.toString());
        }

        editor.apply();

        // Show success message
        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

        // Optionally, go back to the previous screen
        finish();
    }

    // Handle the back button press
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Save dark mode preference
    private void updateTheme(boolean isDarkMode) {
        int defaultNightMode = isDarkMode ?
                AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(defaultNightMode);
    }

    public void editProfile(View view) {
        // code to edit profile (already handled in this activity)
    }

    public void changePassword(View view) {
        // code to change password
    }
}
