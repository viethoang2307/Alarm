package com.example.alarm.service;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.example.alarm.R;
import com.example.alarm.activity.AlarmNotificationActivity;
import com.example.alarm.activity.MathChallengeActivity;
import com.example.alarm.activity.StopAlarmActivity;
import com.example.alarm.model.Alarm;
import com.example.alarm.receiver.AlarmActionReceiver;
import com.example.alarm.utils.NotificationHelper;

import java.io.IOException;

public class AlarmService extends Service {

    private static final String TAG = "AlarmService";
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Alarm alarm;

    private static final int NOTIF_ID = 1;
    private static final String CHANNEL_ID = "alarm_channel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "AlarmService started");
        alarm = (Alarm) intent.getSerializableExtra("alarm");

        if (alarm == null) {
            Log.e(TAG, "Alarm object is null, stopping service");
            stopSelf();
            return START_NOT_STICKY;
        }

        Log.d(TAG, "Alarm received: " + alarm.getLabel());

        // Phát nhạc
        playAlarmSound();

        // Auto dismiss sau N phút (nếu bật)
        if (alarm.getAutoDismissMinutes() > 0) {
            handler.postDelayed(this::stopSelf, alarm.getAutoDismissMinutes() * 60 * 1000);
        }

        // Hiện notification → mở MathChallengeActivity (nếu bật)
        Log.d(TAG, "Showing alarm notification");
        showAlarmNotification();

        return START_NOT_STICKY;
    }

    private void playAlarmSound() {
        try {
            mediaPlayer = new MediaPlayer();

            Uri ringtoneUri;
            if (alarm.isRandomMusic()) {
                // Tùy biến nếu có danh sách nhạc riêng
                ringtoneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.default_alarm);
            } else {
                String ringtonePath = alarm.getRingtonePath();
                if (ringtonePath != null && !ringtonePath.isEmpty()) {
                    ringtoneUri = Uri.parse(ringtonePath);
                } else {
                    // Fallback to default alarm sound if no ringtone path is set
                    ringtoneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.default_alarm);
                }
            }

            mediaPlayer.setDataSource(this, ringtoneUri);
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());

            mediaPlayer.setVolume(alarm.getVolume() / 100f, alarm.getVolume() / 100f);
            mediaPlayer.setLooping(alarm.isLoop());
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
            // If there's an error, try to play default alarm sound
            playDefaultAlarmSound();
        }
    }

    private void playDefaultAlarmSound() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            Uri defaultUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.default_alarm);
            mediaPlayer.setDataSource(this, defaultUri);
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            mediaPlayer.setVolume(0.8f, 0.8f); // Default volume
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlarmNotification() {
        Log.d(TAG, "Creating alarm notification");

        // Check notification permissions
        NotificationHelper.logNotificationStatus(this);
        if (!NotificationHelper.areNotificationsEnabled(this)) {
            Log.e(TAG, "Notifications are disabled for this app!");
        }

        createNotificationChannel();

        // Create snooze intent only (dismiss will be handled in activity)
        Intent snoozeIntent = new Intent(this, AlarmActionReceiver.class);
        snoozeIntent.setAction(AlarmActionReceiver.ACTION_SNOOZE);
        snoozeIntent.putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarm.getId());

        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(
                this, 1, snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Create main content intent (full screen alarm activity)
        Intent mainIntent = new Intent(this, AlarmNotificationActivity.class);
        mainIntent.putExtra("alarm_id", alarm.getId());
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent mainPendingIntent = PendingIntent.getActivity(
                this, 2, mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build notification with full screen intent (no action buttons to avoid trampoline)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("🔔 Báo thức: " + alarm.getLabel())
                .setContentText("Nhấn để " + (alarm.isRequireMathToDismiss() ? "giải toán" : "tắt báo thức"))
                .setContentIntent(mainPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(false)
                .setOngoing(true)
                .setFullScreenIntent(mainPendingIntent, true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // Only add snooze action (dismiss handled in activity)
        builder.addAction(R.drawable.ic_snooze, "Báo lại (5p)", snoozePendingIntent);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIF_ID, builder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
                Log.d(TAG, "Started foreground service with media playback type");
            } else {
                startForeground(NOTIF_ID, builder.build());
                Log.d(TAG, "Started foreground service (legacy)");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to start foreground service", e);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Alarm Channel";
            String desc = "Phát báo thức foreground";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(desc);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            channel.setBypassDnd(true); // Bypass Do Not Disturb
            channel.setShowBadge(true);

            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
