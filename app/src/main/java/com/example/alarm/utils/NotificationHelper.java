package com.example.alarm.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

/**
 * Helper class for notification permissions and settings
 */
public class NotificationHelper {
    
    private static final String TAG = "NotificationHelper";
    
    /**
     * Check if notifications are enabled for the app
     */
    public static boolean areNotificationsEnabled(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        boolean enabled = notificationManager.areNotificationsEnabled();
        Log.d(TAG, "Notifications enabled: " + enabled);
        return enabled;
    }
    
    /**
     * Check if the app can show notifications on lock screen
     */
    public static boolean canShowOnLockScreen(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                int filter = notificationManager.getCurrentInterruptionFilter();
                Log.d(TAG, "Current interruption filter: " + filter);
                return filter != NotificationManager.INTERRUPTION_FILTER_NONE;
            }
        }
        return true;
    }
    
    /**
     * Open notification settings for the app
     */
    public static void openNotificationSettings(Context context) {
        Intent intent = new Intent();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    
    /**
     * Log notification status for debugging
     */
    public static void logNotificationStatus(Context context) {
        Log.d(TAG, "=== NOTIFICATION STATUS ===");
        Log.d(TAG, "Notifications enabled: " + areNotificationsEnabled(context));
        Log.d(TAG, "Can show on lock screen: " + canShowOnLockScreen(context));
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                Log.d(TAG, "Notification channels: " + nm.getNotificationChannels().size());
                nm.getNotificationChannels().forEach(channel -> {
                    Log.d(TAG, "Channel: " + channel.getId() + ", Importance: " + channel.getImportance());
                });
            }
        }
        Log.d(TAG, "=========================");
    }
}
