package com.example.alarm.model;



import java.io.Serializable;

public class Alarm implements Serializable {
    private int id;
    private int hour;
    private int minute;
    private String label;
    private boolean isEnabled;
    private boolean[] repeatDays; // 7 ngày: Chủ nhật đến Thứ bảy
    private String ringtonePath;
    private boolean isRandomMusic;
    private int volume; // 0 - 100
    private boolean isLoop;
    private boolean isIgnoreAfterRing;
    private boolean requireMathToDismiss;
    private int autoSnoozeMinutes;
    private int autoDismissMinutes;

    public Alarm() {
        repeatDays = new boolean[7]; // mặc định không lặp lại
        isEnabled = true;
        volume = 100;
        autoSnoozeMinutes = 5;
        autoDismissMinutes = 3;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }

    public int getMinute() { return minute; }
    public void setMinute(int minute) { this.minute = minute; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }

    public boolean[] getRepeatDays() { return repeatDays; }
    public void setRepeatDays(boolean[] repeatDays) { this.repeatDays = repeatDays; }

    public String getRingtonePath() { return ringtonePath; }
    public void setRingtonePath(String ringtonePath) { this.ringtonePath = ringtonePath; }

    public boolean isRandomMusic() { return isRandomMusic; }
    public void setRandomMusic(boolean randomMusic) { isRandomMusic = randomMusic; }

    public int getVolume() { return volume; }
    public void setVolume(int volume) { this.volume = volume; }

    public boolean isLoop() { return isLoop; }
    public void setLoop(boolean loop) { isLoop = loop; }

    public boolean isIgnoreAfterRing() { return isIgnoreAfterRing; }
    public void setIgnoreAfterRing(boolean ignoreAfterRing) { isIgnoreAfterRing = ignoreAfterRing; }

    public boolean isRequireMathToDismiss() { return requireMathToDismiss; }
    public void setRequireMathToDismiss(boolean requireMathToDismiss) { this.requireMathToDismiss = requireMathToDismiss; }

    public int getAutoSnoozeMinutes() { return autoSnoozeMinutes; }
    public void setAutoSnoozeMinutes(int autoSnoozeMinutes) { this.autoSnoozeMinutes = autoSnoozeMinutes; }

    public int getAutoDismissMinutes() { return autoDismissMinutes; }
    public void setAutoDismissMinutes(int autoDismissMinutes) { this.autoDismissMinutes = autoDismissMinutes; }
}
