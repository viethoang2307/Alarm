package com.example.alarm.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.alarm.R;
import com.example.alarm.database.AlarmDatabaseHelper;
import com.example.alarm.model.Alarm;
import com.example.alarm.util.AlarmUtils;

import java.util.Calendar;

public class QuickAlarmDialog {
    
    public interface OnQuickAlarmSetListener {
        void onAlarmSet(Alarm alarm);
    }
    
    private Context context;
    private OnQuickAlarmSetListener listener;
    
    public QuickAlarmDialog(Context context, OnQuickAlarmSetListener listener) {
        this.context = context;
        this.listener = listener;
    }
    
    public void show() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_quick_alarm, null);
        
        NumberPicker hourPicker = dialogView.findViewById(R.id.hourPicker);
        NumberPicker minutePicker = dialogView.findViewById(R.id.minutePicker);
        
        Button preset5min = dialogView.findViewById(R.id.preset5min);
        Button preset10min = dialogView.findViewById(R.id.preset10min);
        Button preset15min = dialogView.findViewById(R.id.preset15min);
        Button preset30min = dialogView.findViewById(R.id.preset30min);
        Button preset1hour = dialogView.findViewById(R.id.preset1hour);
        Button preset2hour = dialogView.findViewById(R.id.preset2hour);
        
        // Setup number pickers
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 5); // Default to 5 minutes from now
        
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(now.get(Calendar.HOUR_OF_DAY));
        
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(now.get(Calendar.MINUTE));
        
        // Setup preset buttons
        preset5min.setOnClickListener(v -> setPresetTime(hourPicker, minutePicker, 5));
        preset10min.setOnClickListener(v -> setPresetTime(hourPicker, minutePicker, 10));
        preset15min.setOnClickListener(v -> setPresetTime(hourPicker, minutePicker, 15));
        preset30min.setOnClickListener(v -> setPresetTime(hourPicker, minutePicker, 30));
        preset1hour.setOnClickListener(v -> setPresetTime(hourPicker, minutePicker, 60));
        preset2hour.setOnClickListener(v -> setPresetTime(hourPicker, minutePicker, 120));
        
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Báo thức nhanh")
                .setView(dialogView)
                .setPositiveButton("Đặt báo thức", (d, which) -> {
                    createQuickAlarm(hourPicker.getValue(), minutePicker.getValue());
                })
                .setNegativeButton("Hủy", null)
                .create();
                
        dialog.show();
    }
    
    private void setPresetTime(NumberPicker hourPicker, NumberPicker minutePicker, int minutesFromNow) {
        Calendar future = Calendar.getInstance();
        future.add(Calendar.MINUTE, minutesFromNow);
        
        hourPicker.setValue(future.get(Calendar.HOUR_OF_DAY));
        minutePicker.setValue(future.get(Calendar.MINUTE));
    }
    
    private void createQuickAlarm(int hour, int minute) {
        Calendar now = Calendar.getInstance();
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.set(Calendar.MILLISECOND, 0);
        
        // If the time has passed today, set for tomorrow
        if (alarmTime.before(now)) {
            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        Alarm quickAlarm = new Alarm();
        quickAlarm.setHour(hour);
        quickAlarm.setMinute(minute);
        quickAlarm.setLabel("Báo thức nhanh");
        quickAlarm.setEnabled(true);
        quickAlarm.setLoop(false);
        quickAlarm.setVolume(70);
        quickAlarm.setVibrate(true);
        
        // Set repeat days to false (one-time alarm)
        boolean[] repeatDays = new boolean[7];
        quickAlarm.setRepeatDays(repeatDays);
        
        // Save to database
        AlarmDatabaseHelper db = new AlarmDatabaseHelper(context);
        long alarmId = db.insertAlarm(quickAlarm);
        quickAlarm.setId((int) alarmId);

        
        // Schedule the alarm
        AlarmUtils.scheduleAlarm(context, quickAlarm);
        
        // Notify listener
        if (listener != null) {
            listener.onAlarmSet(quickAlarm);
        }
        
        // Show confirmation
        String timeString = String.format("%02d:%02d", hour, minute);
        String message = "Báo thức đã được đặt lúc " + timeString;
        
        // Calculate time until alarm
        long timeDiff = alarmTime.getTimeInMillis() - now.getTimeInMillis();
        long hoursUntil = timeDiff / (1000 * 60 * 60);
        long minutesUntil = (timeDiff % (1000 * 60 * 60)) / (1000 * 60);
        
        if (hoursUntil > 0) {
            message += String.format(" (%d giờ %d phút nữa)", hoursUntil, minutesUntil);
        } else {
            message += String.format(" (%d phút nữa)", minutesUntil);
        }
        
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
