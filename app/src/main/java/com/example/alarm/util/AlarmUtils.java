package com.example.alarm.util;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;


import com.example.alarm.database.AlarmDatabaseHelper;
import com.example.alarm.model.Alarm;
import com.example.alarm.receiver.AlarmReceiver;

import java.util.Calendar;

public class AlarmUtils {

    public static void scheduleAlarm(Context context, Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarm_id", alarm.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        cal.set(Calendar.MINUTE, alarm.getMinute());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1); // hẹn hôm sau nếu giờ đã qua
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }

    public static void cancelAlarm(Context context, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * Schedule a snooze alarm (temporary alarm after X minutes)
     */
    public static void scheduleSnoozeAlarm(Context context, int originalAlarmId, int snoozeMinutes) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Check permission for exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }
        }

        // Get original alarm details
        AlarmDatabaseHelper dbHelper = new AlarmDatabaseHelper(context);
        Alarm originalAlarm = dbHelper.getAlarmById(originalAlarmId);
        if (originalAlarm == null) return;

        // Create snooze alarm with unique ID (original ID + 10000)
        int snoozeAlarmId = originalAlarmId + 10000;

        // Calculate snooze time
        Calendar snoozeTime = Calendar.getInstance();
        snoozeTime.add(Calendar.MINUTE, snoozeMinutes);

        // Create intent for snooze alarm
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarm", originalAlarm); // Pass original alarm data

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, snoozeAlarmId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule the snooze alarm
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                snoozeTime.getTimeInMillis(), pendingIntent);
    }
}
