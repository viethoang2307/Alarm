package com.example.alarm.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.alarm.activity.MathChallengeActivity;
import com.example.alarm.service.AlarmService;

/**
 * BroadcastReceiver to handle alarm notification actions
 */
public class AlarmActionReceiver extends BroadcastReceiver {
    
    private static final String TAG = "AlarmActionReceiver";
    
    public static final String ACTION_DISMISS = "com.example.alarm.ACTION_DISMISS";
    public static final String ACTION_SNOOZE = "com.example.alarm.ACTION_SNOOZE";
    public static final String ACTION_MATH_CHALLENGE = "com.example.alarm.ACTION_MATH_CHALLENGE";
    
    public static final String EXTRA_ALARM_ID = "alarm_id";
    public static final String EXTRA_REQUIRE_MATH = "require_math";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int alarmId = intent.getIntExtra(EXTRA_ALARM_ID, -1);
        boolean requireMath = intent.getBooleanExtra(EXTRA_REQUIRE_MATH, false);
        
        Log.d(TAG, "Received action: " + action + " for alarm ID: " + alarmId);
        
        if (action == null) return;
        
        switch (action) {
            case ACTION_DISMISS:
                if (requireMath) {
                    // Open math challenge activity
                    Intent mathIntent = new Intent(context, MathChallengeActivity.class);
                    mathIntent.putExtra("alarm_id", alarmId);
                    mathIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(mathIntent);
                } else {
                    // Directly stop the alarm service
                    stopAlarmService(context);
                }
                break;
                
            case ACTION_SNOOZE:
                // Snooze for 5 minutes
                snoozeAlarm(context, alarmId);
                break;
                
            case ACTION_MATH_CHALLENGE:
                // Open math challenge activity
                Intent mathIntent = new Intent(context, MathChallengeActivity.class);
                mathIntent.putExtra("alarm_id", alarmId);
                mathIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(mathIntent);
                break;
        }
    }
    
    private void stopAlarmService(Context context) {
        Intent serviceIntent = new Intent(context, AlarmService.class);
        context.stopService(serviceIntent);
        Log.d(TAG, "Alarm service stopped");
    }
    
    private void snoozeAlarm(Context context, int alarmId) {
        // Stop current alarm
        stopAlarmService(context);

        // Schedule snooze alarm for 5 minutes later
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Intent snoozeIntent = new Intent(context, AlarmReceiver.class);
            snoozeIntent.putExtra("alarm_id", alarmId);

            PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId + 10000, // Different request code for snooze
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            long snoozeTime = System.currentTimeMillis() + (5 * 60 * 1000); // 5 minutes

            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeTime, snoozePendingIntent);
                Log.d(TAG, "Snooze alarm scheduled for 5 minutes later");
            } catch (SecurityException e) {
                Log.e(TAG, "Permission denied for setting exact alarm", e);
            }
        }
    }
}
