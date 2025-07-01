package com.example.alarm.database;



import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;


import com.example.alarm.model.Alarm;

import java.util.ArrayList;

public class AlarmDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "alarm_db";
    private static final int DB_VERSION = 2; // Updated version for new fields
    private static final String TABLE_NAME = "alarms";

    public AlarmDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "hour INTEGER," +
                "minute INTEGER," +
                "label TEXT," +
                "enabled INTEGER," +
                "repeat_days TEXT," +
                "ringtone TEXT," +
                "random_music INTEGER," +
                "volume INTEGER," +
                "loop INTEGER," +
                "ignore_after INTEGER," +
                "math_dismiss INTEGER," +
                "auto_snooze INTEGER," +
                "auto_dismiss INTEGER," +
                "favorite_music TEXT," +
                "vibrate INTEGER," +
                "math_difficulty INTEGER," +
                "upcoming_notification INTEGER," +
                "upcoming_notification_minutes INTEGER," +
                "gradual_volume INTEGER," +
                "snooze_count INTEGER," +
                "max_snooze_count INTEGER" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if (oldV < 2) {
            // Add new columns for version 2
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN favorite_music TEXT");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN vibrate INTEGER DEFAULT 1");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN math_difficulty INTEGER DEFAULT 1");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN upcoming_notification INTEGER DEFAULT 1");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN upcoming_notification_minutes INTEGER DEFAULT 15");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN gradual_volume INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN snooze_count INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN max_snooze_count INTEGER DEFAULT 3");
        }
    }

    public long insertAlarm(Alarm alarm) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = toValues(alarm);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public void updateAlarm(Alarm alarm) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = toValues(alarm);
        int rowsAffected = db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(alarm.getId())});
        db.close();
    }

    public void deleteAlarm(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        android.util.Log.d("AlarmDB", "Deleted rows: " + rows);
    }

    public ArrayList<Alarm> getAllAlarms() {
        ArrayList<Alarm> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "hour, minute");
        while (cursor.moveToNext()) {
            Alarm alarm = new Alarm();
            alarm.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            alarm.setHour(cursor.getInt(cursor.getColumnIndexOrThrow("hour")));
            alarm.setMinute(cursor.getInt(cursor.getColumnIndexOrThrow("minute")));
            alarm.setLabel(cursor.getString(cursor.getColumnIndexOrThrow("label")));
            alarm.setEnabled(cursor.getInt(cursor.getColumnIndexOrThrow("enabled")) == 1);
            alarm.setRepeatDays(parseRepeatDays(cursor.getString(cursor.getColumnIndexOrThrow("repeat_days"))));
            String ringtonePath = cursor.getString(cursor.getColumnIndexOrThrow("ringtone"));
            alarm.setRingtonePath(ringtonePath);
            alarm.setRandomMusic(cursor.getInt(cursor.getColumnIndexOrThrow("random_music")) == 1);
            alarm.setVolume(cursor.getInt(cursor.getColumnIndexOrThrow("volume")));
            alarm.setLoop(cursor.getInt(cursor.getColumnIndexOrThrow("loop")) == 1);
            alarm.setIgnoreAfterRing(cursor.getInt(cursor.getColumnIndexOrThrow("ignore_after")) == 1);
            alarm.setRequireMathToDismiss(cursor.getInt(cursor.getColumnIndexOrThrow("math_dismiss")) == 1);
            alarm.setAutoSnoozeMinutes(cursor.getInt(cursor.getColumnIndexOrThrow("auto_snooze")));
            alarm.setAutoDismissMinutes(cursor.getInt(cursor.getColumnIndexOrThrow("auto_dismiss")));

            // Handle new fields with null checks for backward compatibility
            int favoriteIndex = cursor.getColumnIndex("favorite_music");
            if (favoriteIndex != -1) {
                alarm.setFavoriteMusicPath(cursor.getString(favoriteIndex));
                alarm.setVibrate(cursor.getInt(cursor.getColumnIndexOrThrow("vibrate")) == 1);
                alarm.setMathDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow("math_difficulty")));
                alarm.setShowUpcomingNotification(cursor.getInt(cursor.getColumnIndexOrThrow("upcoming_notification")) == 1);
                alarm.setUpcomingNotificationMinutes(cursor.getInt(cursor.getColumnIndexOrThrow("upcoming_notification_minutes")));
                alarm.setGradualVolumeIncrease(cursor.getInt(cursor.getColumnIndexOrThrow("gradual_volume")) == 1);
                alarm.setSnoozeCount(cursor.getInt(cursor.getColumnIndexOrThrow("snooze_count")));
                alarm.setMaxSnoozeCount(cursor.getInt(cursor.getColumnIndexOrThrow("max_snooze_count")));
            }

            list.add(alarm);
        }
        cursor.close();
        db.close();
        return list;
    }

    public Alarm getAlarmById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            Alarm alarm = new Alarm();
            alarm.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            alarm.setHour(cursor.getInt(cursor.getColumnIndexOrThrow("hour")));
            alarm.setMinute(cursor.getInt(cursor.getColumnIndexOrThrow("minute")));
            alarm.setLabel(cursor.getString(cursor.getColumnIndexOrThrow("label")));
            alarm.setEnabled(cursor.getInt(cursor.getColumnIndexOrThrow("enabled")) == 1);
            alarm.setRepeatDays(parseRepeatDays(cursor.getString(cursor.getColumnIndexOrThrow("repeat_days"))));
            String ringtonePath = cursor.getString(cursor.getColumnIndexOrThrow("ringtone"));
            alarm.setRingtonePath(ringtonePath);
            alarm.setRandomMusic(cursor.getInt(cursor.getColumnIndexOrThrow("random_music")) == 1);
            alarm.setVolume(cursor.getInt(cursor.getColumnIndexOrThrow("volume")));
            alarm.setLoop(cursor.getInt(cursor.getColumnIndexOrThrow("loop")) == 1);
            alarm.setIgnoreAfterRing(cursor.getInt(cursor.getColumnIndexOrThrow("ignore_after")) == 1);
            alarm.setRequireMathToDismiss(cursor.getInt(cursor.getColumnIndexOrThrow("math_dismiss")) == 1);
            alarm.setAutoSnoozeMinutes(cursor.getInt(cursor.getColumnIndexOrThrow("auto_snooze")));
            alarm.setAutoDismissMinutes(cursor.getInt(cursor.getColumnIndexOrThrow("auto_dismiss")));

            // Handle new fields with null checks for backward compatibility
            int favoriteIndex = cursor.getColumnIndex("favorite_music");
            if (favoriteIndex != -1) {
                alarm.setFavoriteMusicPath(cursor.getString(favoriteIndex));
                alarm.setVibrate(cursor.getInt(cursor.getColumnIndexOrThrow("vibrate")) == 1);
                alarm.setMathDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow("math_difficulty")));
                alarm.setShowUpcomingNotification(cursor.getInt(cursor.getColumnIndexOrThrow("upcoming_notification")) == 1);
                alarm.setUpcomingNotificationMinutes(cursor.getInt(cursor.getColumnIndexOrThrow("upcoming_notification_minutes")));
                alarm.setGradualVolumeIncrease(cursor.getInt(cursor.getColumnIndexOrThrow("gradual_volume")) == 1);
                alarm.setSnoozeCount(cursor.getInt(cursor.getColumnIndexOrThrow("snooze_count")));
                alarm.setMaxSnoozeCount(cursor.getInt(cursor.getColumnIndexOrThrow("max_snooze_count")));
            }

            cursor.close();
            db.close();
            return alarm;
        }
        cursor.close();
        db.close();
        return null;
    }

    private ContentValues toValues(Alarm alarm) {
        ContentValues values = new ContentValues();
        values.put("hour", alarm.getHour());
        values.put("minute", alarm.getMinute());
        values.put("label", alarm.getLabel());
        values.put("enabled", alarm.isEnabled() ? 1 : 0);
        values.put("repeat_days", stringifyRepeatDays(alarm.getRepeatDays()));
        values.put("ringtone", alarm.getRingtonePath());
        values.put("random_music", alarm.isRandomMusic() ? 1 : 0);
        values.put("volume", alarm.getVolume());
        values.put("loop", alarm.isLoop() ? 1 : 0);
        values.put("ignore_after", alarm.isIgnoreAfterRing() ? 1 : 0);
        values.put("math_dismiss", alarm.isRequireMathToDismiss() ? 1 : 0);
        values.put("auto_snooze", alarm.getAutoSnoozeMinutes());
        values.put("auto_dismiss", alarm.getAutoDismissMinutes());

        // Add new fields
        values.put("favorite_music", alarm.getFavoriteMusicPath());
        values.put("vibrate", alarm.isVibrate() ? 1 : 0);
        values.put("math_difficulty", alarm.getMathDifficulty());
        values.put("upcoming_notification", alarm.isShowUpcomingNotification() ? 1 : 0);
        values.put("upcoming_notification_minutes", alarm.getUpcomingNotificationMinutes());
        values.put("gradual_volume", alarm.isGradualVolumeIncrease() ? 1 : 0);
        values.put("snooze_count", alarm.getSnoozeCount());
        values.put("max_snooze_count", alarm.getMaxSnoozeCount());

        return values;
    }

    private String stringifyRepeatDays(boolean[] days) {
        StringBuilder sb = new StringBuilder();
        for (boolean b : days) sb.append(b ? "1" : "0");
        return sb.toString(); // Ví dụ: 0110010
    }

    private boolean[] parseRepeatDays(String str) {
        boolean[] days = new boolean[7];
        for (int i = 0; i < 7 && i < str.length(); i++) {
            days[i] = str.charAt(i) == '1';
        }
        return days;
    }
}
