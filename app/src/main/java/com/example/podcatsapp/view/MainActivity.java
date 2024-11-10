package com.example.podcatsapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.podcatsapp.R;
import com.example.podcatsapp.controller.PublicationsAdapter;
import com.example.podcatsapp.model.Category;
import com.example.podcatsapp.model.Publication;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String DARK_MODE_KEY = "dark_mode_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize theme before setContentView
        initializeTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lookup the recyclerview in activity layout
        RecyclerView rvPublications = (RecyclerView) findViewById(R.id.rec_view);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Initialize publications list
        ArrayList<Publication> pubList = initializePublicationsList();

        // Create adapter passing in the sample user data
        PublicationsAdapter adapter = new PublicationsAdapter(pubList);
        // Attach the adapter to the recyclerview to populate items
        rvPublications.setAdapter(adapter);
        // Set layout manager to position the items
        rvPublications.setLayoutManager(new LinearLayoutManager(this));

        // Bottom menu setup
        setupBottomNavigation(bottomNavigationView);
    }

    private void initializeTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        int defaultNightMode = isDarkMode ?
                AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(defaultNightMode);
    }

    private ArrayList<Publication> initializePublicationsList() {
        ArrayList<Publication> pubList = new ArrayList<>();
        pubList.add(new Publication("Publication 1", "Descriptions1", R.drawable.cat1, true, Category.PODCAST));
        pubList.add(new Publication("Publication 2", "Descriptions2", R.drawable.cat2, true, Category.VIDEO));
        pubList.add(new Publication("Publication 3", "Descriptions3", R.drawable.cat3, true, Category.VIDEO));
        pubList.add(new Publication("Publication 4", "Descriptions4", R.drawable.cat3, true, Category.PODCAST));
        pubList.add(new Publication("Publication 4", "Descriptions5", R.drawable.cat3, true, Category.PODCAST));
        pubList.add(new Publication("Publication 4", "Descriptions6", R.drawable.cat3, true, Category.PODCAST));
        pubList.add(new Publication("Publication 4", "Descriptions7", R.drawable.cat3, true, Category.PODCAST));
        pubList.add(new Publication("Publication 4", "Descriptions8", R.drawable.cat3, true, Category.PODCAST));
        pubList.add(new Publication("Publication 4", "Descriptions9", R.drawable.cat3, true, Category.PODCAST));
        pubList.add(new Publication("Publication 4", "Descriptions10", R.drawable.cat3, true, Category.PODCAST));
        return pubList;
    }

    private void setupBottomNavigation(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.item_home) {
                return true; // Already on Home
            } else if (id == R.id.item_videos) {
                startActivity(new Intent(MainActivity.this, PlayerActivity.class));
                return true;
            } else if (id == R.id.item_upload) {
                startActivity(new Intent(MainActivity.this, PlayerActivity.class));
                return true;
            } else if (id == R.id.item_setting) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.item_home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if theme changed while away from activity
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        int currentNightMode = AppCompatDelegate.getDefaultNightMode();
        boolean isCurrentlyInDarkMode = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES;

        // If there's a mismatch, update the theme
        if (isDarkMode != isCurrentlyInDarkMode) {
            recreate();
        }
    }
}