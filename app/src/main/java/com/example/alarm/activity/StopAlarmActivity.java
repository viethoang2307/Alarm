package com.example.alarm.activity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alarm.R;
import com.example.alarm.service.AlarmService;


public class StopAlarmActivity extends AppCompatActivity {

    private Button stopAlarmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_alarm);

        stopAlarmButton = findViewById(R.id.stopAlarmButton);

        stopAlarmButton.setOnClickListener(v -> {
            stopService(new Intent(this, AlarmService.class));
            finish();
        });
    }
}
