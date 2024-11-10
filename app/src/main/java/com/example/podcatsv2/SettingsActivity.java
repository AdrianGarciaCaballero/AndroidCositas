package com.example.podcatsv2;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
// SettingsActivity.java

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {
    private EditText editTextUpdateName, editTextUpdatePassword;
    private Button buttonUpdate,btnLogOut;
    private Spinner spinnerLanguage;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());

        // Initialize UI elements
        editTextUpdateName = findViewById(R.id.editTextUpdateName);
        editTextUpdatePassword = findViewById(R.id.editTextUpdatePassword);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        btnLogOut = findViewById(R.id.bntLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
        // Set up Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        // Set current language selection
       /* String currentLanguage = LocaleManager.getLanguage(this);
        if (currentLanguage.equals("es")) {
            spinnerLanguage.setSelection(1);
        } else {
            spinnerLanguage.setSelection(0);
        }*/

        // Set up listeners
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleUpdate();
            }
        });
        /*
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirstSelection = true; // To prevent initial callback

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return;
                }
                String selectedLanguage = position == 1 ? "es" : "en";
                LocaleManager.setLocale(SettingsActivity.this, selectedLanguage);
                Toast.makeText(SettingsActivity.this, getString(R.string.language_changed), Toast.LENGTH_SHORT).show();
                recreate(); // Recreate activity to apply changes
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });*/

        // menu
        // Inside onCreate() method
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_feed); // Set current selected item

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_settings) {
                // Already on Feed, do nothing
                return true;
            } else if (itemId == R.id.nav_video) {
                // Navigate to UploadVideosActivity
                Intent uploadIntent = new Intent(SettingsActivity.this, UploadVideosActivity.class);
                startActivity(uploadIntent);
                overridePendingTransition(0, 0); // Optional: Disable transition animation
                return true;
            } else if (itemId == R.id.nav_feed) {
                // Navigate to SettingsActivity
                Intent settingsIntent = new Intent(SettingsActivity.this, VideoFeedActivity.class);
                startActivity(settingsIntent);
                overridePendingTransition(0, 0); // Optional: Disable transition animation
                return true;
            }

            return false;
        });

    }

    private void handleUpdate() {
        final String newName = editTextUpdateName.getText().toString().trim();
        final String newPassword = editTextUpdatePassword.getText().toString().trim();

        boolean valid = true;

        if (newName.isEmpty()) {
            editTextUpdateName.setError(getString(R.string.name_required));
            valid = false;
        }

        if (!newPassword.isEmpty() && newPassword.length() < 6) {
            editTextUpdatePassword.setError(getString(R.string.password_length));
            valid = false;
        }

        if (!valid) return;

        if (!newName.isEmpty()) {
            // Update name in Realtime Database
            usersRef.child("name").setValue(newName);
        }

        if (!newPassword.isEmpty()) {
            // Update password in Firebase Authentication
            mAuth.getCurrentUser().updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, getString(R.string.password_updated), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingsActivity.this, getString(R.string.password_update_failed), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        Toast.makeText(this, getString(R.string.settings_updated), Toast.LENGTH_SHORT).show();
    }
    private void logoutUser() {
        // Log out the user from Firebase
        FirebaseAuth.getInstance().signOut();

        // Navigate to LoginActivity
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
       // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Close the current activity
    }

}
