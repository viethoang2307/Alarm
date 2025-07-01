package com.example.alarm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alarm.R;
import com.example.alarm.database.AlarmDatabaseHelper;
import com.example.alarm.model.Alarm;
import com.example.alarm.service.AlarmService;
import com.example.alarm.util.AlarmUtils;

/**
 * Full screen activity that shows when alarm triggers
 * Replaces notification action buttons to avoid Android 12+ trampoline restrictions
 */
public class AlarmNotificationActivity extends AppCompatActivity {

    private Alarm alarm;
    private TextView titleText;
    private TextView timeText;
    private Button dismissButton;
    private Button snoozeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Show over lock screen
        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
        
        setContentView(R.layout.activity_alarm_notification);

        // Get alarm from intent
        int alarmId = getIntent().getIntExtra("alarm_id", -1);
        if (alarmId == -1) {
            finish();
            return;
        }

        AlarmDatabaseHelper dbHelper = new AlarmDatabaseHelper(this);
        alarm = dbHelper.getAlarmById(alarmId);
        if (alarm == null) {
            finish();
            return;
        }

        initViews();
        setupButtons();
    }

    private void initViews() {
        titleText = findViewById(R.id.titleText);
        timeText = findViewById(R.id.timeText);
        dismissButton = findViewById(R.id.dismissButton);
        snoozeButton = findViewById(R.id.snoozeButton);

        titleText.setText(alarm.getLabel());
        timeText.setText(String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));

        // Update dismiss button text based on math requirement
        if (alarm.isRequireMathToDismiss()) {
            dismissButton.setText("🧮 Giải toán để tắt");
            dismissButton.setBackgroundColor(getColor(R.color.orange));
        } else {
            dismissButton.setText("⏹️ Tắt báo thức");
            dismissButton.setBackgroundColor(getColor(R.color.red));
        }
    }

    private void setupButtons() {
        dismissButton.setOnClickListener(v -> {
            if (alarm.isRequireMathToDismiss()) {
                // Open math challenge
                Intent mathIntent = new Intent(this, MathChallengeActivity.class);
                mathIntent.putExtra("alarm_id", alarm.getId());
                startActivity(mathIntent);
            } else {
                // Stop alarm directly
                stopAlarm();
            }
            finish();
        });

        snoozeButton.setOnClickListener(v -> {
            snoozeAlarm();
            finish();
        });
    }

    private void stopAlarm() {
        // Stop the alarm service
        Intent serviceIntent = new Intent(this, AlarmService.class);
        stopService(serviceIntent);
    }

    private void snoozeAlarm() {
        // Stop current alarm
        stopAlarm();
        
        // Schedule snooze alarm (5 minutes later)
        AlarmUtils.scheduleSnoozeAlarm(this, alarm.getId(), 5);
    }

    @Override
    public void onBackPressed() {
        // Prevent back button from closing alarm
        // User must use dismiss or snooze buttons
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Make sure to stop alarm if activity is destroyed
        stopAlarm();
    }
}
