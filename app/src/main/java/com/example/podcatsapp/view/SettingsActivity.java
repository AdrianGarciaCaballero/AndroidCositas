package com.example.podcatsapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.podcatsapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {
    private Switch switchNotifications;
    private Switch switchDarkMode;
    private SharedPreferences sharedPreferences;
    private static final String DARK_MODE_KEY = "dark_mode_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // Set the theme before setting content view
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        updateTheme(isDarkMode);

        setContentView(R.layout.activity_settings);

        switchNotifications = findViewById(R.id.switch_notifications);
        switchDarkMode = findViewById(R.id.switch_dark_mode);

        // Set initial switch state based on saved preference
        switchDarkMode.setChecked(isDarkMode);

        // Initialize the thumb tint colors
        setupSwitchTint(switchNotifications);
        setupSwitchTint(switchDarkMode);

        // Set listeners to change thumb tint on switch toggle
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setSwitchThumbColor(switchNotifications, isChecked);
            // Add additional notification logic here if needed
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setSwitchThumbColor(switchDarkMode, isChecked);
            saveDarkModePreference(isChecked);
            updateTheme(isChecked);
            recreate(); // Recreate the activity to apply the theme
        });

        // Setup Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        setupBottomNavigation(bottomNavigationView);
    }

    private void setupSwitchTint(Switch switchComponent) {
        setSwitchThumbColor(switchComponent, switchComponent.isChecked());
    }

    private void setSwitchThumbColor(Switch switchComponent, boolean isChecked) {
        if (isChecked) {
            switchComponent.getThumbDrawable().setTint(Color.parseColor("#FF007A")); // Pink
        } else {
            switchComponent.getThumbDrawable().setTint(Color.parseColor("#424242")); // Gray
        }
    }

    private void saveDarkModePreference(boolean isDarkMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DARK_MODE_KEY, isDarkMode);
        editor.apply();
    }

    private void updateTheme(boolean isDarkMode) {
        int defaultNightMode = isDarkMode ?
                AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(defaultNightMode);
    }

    // Method to navigate to EditProfileActivity
    public void editProfile(View view) {
        Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void changePassword(View view) {
        // Add logic to handle password change
    }

    // Method to set up the Bottom Navigation
    private void setupBottomNavigation(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.item_home) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                return true;
            } else if (id == R.id.item_videos) {
                startActivity(new Intent(SettingsActivity.this, PlayerActivity.class));
                return true;
            } else if (id == R.id.item_upload) {
                startActivity(new Intent(SettingsActivity.this, UploadActivity.class));
                return true;
            } else if (id == R.id.item_setting) {
                return true; // Already in SettingsActivity
            }
            return false;
        });
    }
}
