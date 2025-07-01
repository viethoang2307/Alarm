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


import com.example.alarm.activity.AlarmEditActivity;
import com.example.alarm.adapter.AlarmListAdapter;
import com.example.alarm.database.AlarmDatabaseHelper;
import com.example.alarm.model.Alarm;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView alarmListView;
    private ImageButton addAlarmButton;
    private AlarmListAdapter adapter;
    private AlarmDatabaseHelper dbHelper;
    private ArrayList<Alarm> alarmList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmListView = findViewById(R.id.alarmListView);
        addAlarmButton = findViewById(R.id.addAlarmButton);

        dbHelper = new AlarmDatabaseHelper(this);
        alarmList = dbHelper.getAllAlarms();
        adapter = new AlarmListAdapter(this, alarmList);
        alarmListView.setAdapter(adapter);

        addAlarmButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AlarmEditActivity.class);
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
    }

    private void showDeleteDialog(Alarm alarm) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa báo thức?")
                .setMessage("Bạn có chắc chắn muốn xóa báo thức này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteAlarm(alarm.getId());
                    alarmList.clear();
                    alarmList.addAll(dbHelper.getAllAlarms());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Đã xóa báo thức", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        alarmList.clear();
        alarmList.addAll(dbHelper.getAllAlarms());
        adapter.notifyDataSetChanged();
    }
}
