package com.example.alarm.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.alarm.R;
import com.example.alarm.model.Alarm;
import com.example.alarm.service.AlarmService;
import com.example.alarm.utils.NotificationHelper;

/**
 * Test activity for notification permissions and functionality
 */
public class NotificationTestActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST = 1001;
    private TextView statusText;
    private Button testButton;
    private Button permissionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_test);

        statusText = findViewById(R.id.statusText);
        testButton = findViewById(R.id.testButton);
        permissionButton = findViewById(R.id.permissionButton);

        updateStatus();

        testButton.setOnClickListener(v -> testNotification());
        permissionButton.setOnClickListener(v -> requestNotificationPermission());
    }

    private void updateStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Notification Status:\n\n");
        
        boolean enabled = NotificationHelper.areNotificationsEnabled(this);
        status.append("✓ Notifications Enabled: ").append(enabled ? "YES" : "NO").append("\n");
        
        boolean lockScreen = NotificationHelper.canShowOnLockScreen(this);
        status.append("✓ Lock Screen: ").append(lockScreen ? "YES" : "NO").append("\n");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean hasPermission = ContextCompat.checkSelfPermission(this, 
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
            status.append("✓ POST_NOTIFICATIONS: ").append(hasPermission ? "YES" : "NO").append("\n");
        }
        
        statusText.setText(status.toString());
        
        testButton.setEnabled(enabled);
        permissionButton.setEnabled(!enabled);
    }

    private void testNotification() {
        // Create a test alarm
        Alarm testAlarm = new Alarm();
        testAlarm.setId(999);
        testAlarm.setLabel("Test Notification");
        testAlarm.setRequireMathToDismiss(false);
        testAlarm.setHour(12);
        testAlarm.setMinute(0);

        // Start AlarmService to show notification
        Intent serviceIntent = new Intent(this, AlarmService.class);
        serviceIntent.putExtra("alarm", testAlarm);
        
        try {
            startService(serviceIntent);
            Toast.makeText(this, "Test notification started!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                
                ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 
                    NOTIFICATION_PERMISSION_REQUEST);
            } else {
                NotificationHelper.openNotificationSettings(this);
            }
        } else {
            NotificationHelper.openNotificationSettings(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show();
            }
            updateStatus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }
}
