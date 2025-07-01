package com.example.alarm.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.alarm.MainActivity;
import com.example.alarm.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure consistent theme across all activities
        setTheme(R.style.Theme_Alarm);
    }

    protected void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_alarm) {
                    if (!(this instanceof MainActivity)) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                    return true;
                } else if (id == R.id.menu_timer) {
                    if (!(this instanceof TimerActivity)) {
                        startActivity(new Intent(this, TimerActivity.class));
                        finish();
                    }
                    return true;
                } else if (id == R.id.menu_stopwatch) {
                    if (!(this instanceof StopwatchActivity)) {
                        startActivity(new Intent(this, StopwatchActivity.class));
                        finish();
                    }
                    return true;
                }
                return false;
            });
        }
    }

    protected void setSelectedNavigationItem(int menuItemId) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(menuItemId);
        }
    }
}
