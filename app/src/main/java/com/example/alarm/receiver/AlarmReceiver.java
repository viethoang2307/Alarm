package com.example.alarm.receiver;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.alarm.database.AlarmDatabaseHelper;
import com.example.alarm.model.Alarm;
import com.example.alarm.service.AlarmService;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmId = intent.getIntExtra("alarm_id", -1);
        if (alarmId != -1) {
            AlarmDatabaseHelper db = new AlarmDatabaseHelper(context);
            Alarm alarm = db.getAlarmById(alarmId);
            if (alarm != null && alarm.isEnabled()) {
                Log.d("AlarmReceiver", "Báo thức được kích hoạt: ID = " + alarmId);

                // Gọi AlarmService để xử lý phát nhạc
                Intent serviceIntent = new Intent(context, AlarmService.class);
                serviceIntent.putExtra("alarm", alarm);
                context.startService(serviceIntent);
            }
        }
    }
}
