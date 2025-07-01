package com.example.alarm.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.content.Intent;
import android.media.RingtoneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicUtils {
    
    public static class MusicItem {
        public String title;
        public String artist;
        public String path;
        public long duration;
        
        public MusicItem(String title, String artist, String path, long duration) {
            this.title = title;
            this.artist = artist;
            this.path = path;
            this.duration = duration;
        }
        
        @Override
        public String toString() {
            return title + (artist != null && !artist.isEmpty() ? " - " + artist : "");
        }
    }
    
    public static List<MusicItem> getAllMusicFiles(Context context) {
        List<MusicItem> musicList = new ArrayList<>();
        
        String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
        };
        
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        
        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder)) {
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    
                    musicList.add(new MusicItem(title, artist, path, duration));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return musicList;
    }
    
    public static MusicItem getRandomMusic(Context context) {
        List<MusicItem> musicList = getAllMusicFiles(context);
        if (!musicList.isEmpty()) {
            Random random = new Random();
            return musicList.get(random.nextInt(musicList.size()));
        }
        return null;
    }
    
    public static Intent createRingtonePickerIntent() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Chọn nhạc chuông");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        return intent;
    }
    
    public static String getRingtoneTitle(Context context, Uri ringtoneUri) {
        if (ringtoneUri == null) return "Mặc định";
        
        try {
            return RingtoneManager.getRingtone(context, ringtoneUri).getTitle(context);
        } catch (Exception e) {
            return "Không xác định";
        }
    }
    
    public static String formatDuration(long duration) {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    public static boolean isValidMusicFile(String path) {
        if (path == null || path.isEmpty()) return false;
        
        String lowerPath = path.toLowerCase();
        return lowerPath.endsWith(".mp3") || 
               lowerPath.endsWith(".wav") || 
               lowerPath.endsWith(".m4a") || 
               lowerPath.endsWith(".aac") ||
               lowerPath.endsWith(".ogg") ||
               lowerPath.endsWith(".flac");
    }
}
