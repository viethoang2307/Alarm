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
    private static final int DB_VERSION = 1;
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
                "auto_dismiss INTEGER" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertAlarm(Alarm alarm) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = toValues(alarm);
        db.insert(TABLE_NAME, null, values);
    }

    public void updateAlarm(Alarm alarm) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = toValues(alarm);
        db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(alarm.getId())});
    }

    public void deleteAlarm(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)});
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
            alarm.setRingtonePath(cursor.getString(cursor.getColumnIndexOrThrow("ringtone")));
            alarm.setRandomMusic(cursor.getInt(cursor.getColumnIndexOrThrow("random_music")) == 1);
            alarm.setVolume(cursor.getInt(cursor.getColumnIndexOrThrow("volume")));
            alarm.setLoop(cursor.getInt(cursor.getColumnIndexOrThrow("loop")) == 1);
            alarm.setIgnoreAfterRing(cursor.getInt(cursor.getColumnIndexOrThrow("ignore_after")) == 1);
            alarm.setRequireMathToDismiss(cursor.getInt(cursor.getColumnIndexOrThrow("math_dismiss")) == 1);
            alarm.setAutoSnoozeMinutes(cursor.getInt(cursor.getColumnIndexOrThrow("auto_snooze")));
            alarm.setAutoDismissMinutes(cursor.getInt(cursor.getColumnIndexOrThrow("auto_dismiss")));
            list.add(alarm);
        }
        cursor.close();
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
            alarm.setRingtonePath(cursor.getString(cursor.getColumnIndexOrThrow("ringtone")));
            alarm.setRandomMusic(cursor.getInt(cursor.getColumnIndexOrThrow("random_music")) == 1);
            alarm.setVolume(cursor.getInt(cursor.getColumnIndexOrThrow("volume")));
            alarm.setLoop(cursor.getInt(cursor.getColumnIndexOrThrow("loop")) == 1);
            alarm.setIgnoreAfterRing(cursor.getInt(cursor.getColumnIndexOrThrow("ignore_after")) == 1);
            alarm.setRequireMathToDismiss(cursor.getInt(cursor.getColumnIndexOrThrow("math_dismiss")) == 1);
            alarm.setAutoSnoozeMinutes(cursor.getInt(cursor.getColumnIndexOrThrow("auto_snooze")));
            alarm.setAutoDismissMinutes(cursor.getInt(cursor.getColumnIndexOrThrow("auto_dismiss")));
            cursor.close();
            return alarm;
        }
        cursor.close();
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
