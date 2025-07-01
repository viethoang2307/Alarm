package com.example.alarm.service;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.example.alarm.R;
import com.example.alarm.activity.MathChallengeActivity;
import com.example.alarm.activity.StopAlarmActivity;
import com.example.alarm.model.Alarm;

import java.io.IOException;

public class AlarmService extends Service {

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Alarm alarm;

    private static final int NOTIF_ID = 1;
    private static final String CHANNEL_ID = "alarm_channel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarm = (Alarm) intent.getSerializableExtra("alarm");

        if (alarm == null) stopSelf();

        // Phát nhạc
        playAlarmSound();

        // Auto dismiss sau N phút (nếu bật)
        if (alarm.getAutoDismissMinutes() > 0) {
            handler.postDelayed(this::stopSelf, alarm.getAutoDismissMinutes() * 60 * 1000);
        }

        // Hiện notification → mở MathChallengeActivity (nếu bật)
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
                ringtoneUri = Uri.parse(alarm.getRingtonePath());
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
        }
    }

    private void showAlarmNotification() {
        createNotificationChannel();

        Intent dismissIntent = new Intent(this, alarm.isRequireMathToDismiss()
                ? MathChallengeActivity.class
                : StopAlarmActivity.class); // StopAlarmActivity sẽ chỉ gọi stopService

        dismissIntent.putExtra("alarm_id", alarm.getId());

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("Báo thức: " + alarm.getLabel())
                .setContentText("Nhấn để tắt báo thức")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)
                .setOngoing(true);

        startForeground(NOTIF_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Alarm Channel";
            String desc = "Phát báo thức foreground";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(desc);

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
