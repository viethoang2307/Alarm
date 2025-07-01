package com.example.alarm;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alarm.activity.BaseActivity;
import com.example.alarm.activity.AlarmEditActivity;
import com.example.alarm.activity.NotificationTestActivity;
import com.example.alarm.activity.StopwatchActivity;
import com.example.alarm.activity.TimerActivity;
import com.example.alarm.adapter.AlarmListAdapter;
import com.example.alarm.database.AlarmDatabaseHelper;
import com.example.alarm.model.Alarm;
import com.example.alarm.dialog.QuickAlarmDialog;
import com.example.alarm.util.AlarmUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements AlarmListAdapter.OnAlarmActionListener {

    private ListView alarmListView;
    private ImageButton addAlarmButton, quickAlarmButton, testNotificationButton;
    private AlarmListAdapter adapter;
    private AlarmDatabaseHelper dbHelper;
    private ArrayList<Alarm> alarmList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmListView = findViewById(R.id.alarmListView);
        addAlarmButton = findViewById(R.id.addAlarmButton);
        quickAlarmButton = findViewById(R.id.quickAlarmButton);
        testNotificationButton = findViewById(R.id.testNotificationButton);

        dbHelper = new AlarmDatabaseHelper(this);
        alarmList = dbHelper.getAllAlarms();
        adapter = new AlarmListAdapter(this, alarmList);
        adapter.setOnAlarmActionListener(this);
        alarmListView.setAdapter(adapter);

        addAlarmButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AlarmEditActivity.class);
            startActivity(intent);
        });

        quickAlarmButton.setOnClickListener(v -> showQuickAlarmDialog());

        testNotificationButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationTestActivity.class);
            startActivity(intent);
        });

        alarmListView.setOnItemClickListener((parent, view, position, id) -> {
            Alarm alarm = alarmList.get(position);
            Intent intent = new Intent(MainActivity.this, AlarmEditActivity.class);
            intent.putExtra("alarm_id", alarm.getId());
            startActivity(intent);
        });

        alarmListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Alarm alarm = alarmList.get(position);
            showDeleteDialog(alarm);
            return true;
        });

        setupBottomNavigation();
        setSelectedNavigationItem(R.id.menu_alarm);
    }

    private void showDeleteDialog(Alarm alarm) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa báo thức?")
                .setMessage("Bạn có chắc chắn muốn xóa báo thức này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Cancel the scheduled alarm first
                    AlarmUtils.cancelAlarm(this, alarm.getId());
                    // Delete from database
                    dbHelper.deleteAlarm(alarm.getId());
                    // Refresh the list
                    alarmList.clear();
                    alarmList.addAll(dbHelper.getAllAlarms());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Đã xóa báo thức", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showQuickAlarmDialog() {
        QuickAlarmDialog quickAlarmDialog = new QuickAlarmDialog(this, alarm -> {
            // Refresh the alarm list when a new quick alarm is set
            alarmList.clear();
            alarmList.addAll(dbHelper.getAllAlarms());
            adapter.notifyDataSetChanged();
        });
        quickAlarmDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        alarmList.clear();
        alarmList.addAll(dbHelper.getAllAlarms());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEditAlarm(Alarm alarm) {
        Intent intent = new Intent(MainActivity.this, AlarmEditActivity.class);
        intent.putExtra("alarm_id", alarm.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteAlarm(Alarm alarm) {
        showDeleteDialog(alarm);
    }
}
