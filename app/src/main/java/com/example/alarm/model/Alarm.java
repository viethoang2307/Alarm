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

    // New enhanced features
    private String favoriteMusicPath;
    private boolean isVibrate;
    private int mathDifficulty; // 1=Easy, 2=Medium, 3=Hard
    private boolean showUpcomingNotification;
    private int upcomingNotificationMinutes; // Minutes before alarm to show notification
    private boolean isGradualVolumeIncrease;
    private int snoozeCount;
    private int maxSnoozeCount;

    public Alarm() {
        repeatDays = new boolean[7]; // mặc định không lặp lại
        isEnabled = true;
        volume = 100;
        autoSnoozeMinutes = 5;
        autoDismissMinutes = 3;
        ringtonePath = null; // Will use default alarm sound
        isRandomMusic = false;
        isLoop = true;

        // Initialize new features with defaults
        isVibrate = true;
        mathDifficulty = 1; // Easy by default
        showUpcomingNotification = true;
        upcomingNotificationMinutes = 15; // 15 minutes before
        isGradualVolumeIncrease = false;
        snoozeCount = 0;
        maxSnoozeCount = 3;
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

    // New getters and setters
    public String getFavoriteMusicPath() { return favoriteMusicPath; }
    public void setFavoriteMusicPath(String favoriteMusicPath) { this.favoriteMusicPath = favoriteMusicPath; }

    public boolean isVibrate() { return isVibrate; }
    public void setVibrate(boolean vibrate) { isVibrate = vibrate; }

    public int getMathDifficulty() { return mathDifficulty; }
    public void setMathDifficulty(int mathDifficulty) { this.mathDifficulty = mathDifficulty; }

    public boolean isShowUpcomingNotification() { return showUpcomingNotification; }
    public void setShowUpcomingNotification(boolean showUpcomingNotification) { this.showUpcomingNotification = showUpcomingNotification; }

    public int getUpcomingNotificationMinutes() { return upcomingNotificationMinutes; }
    public void setUpcomingNotificationMinutes(int upcomingNotificationMinutes) { this.upcomingNotificationMinutes = upcomingNotificationMinutes; }

    public boolean isGradualVolumeIncrease() { return isGradualVolumeIncrease; }
    public void setGradualVolumeIncrease(boolean gradualVolumeIncrease) { isGradualVolumeIncrease = gradualVolumeIncrease; }

    public int getSnoozeCount() { return snoozeCount; }
    public void setSnoozeCount(int snoozeCount) { this.snoozeCount = snoozeCount; }

    public int getMaxSnoozeCount() { return maxSnoozeCount; }
    public void setMaxSnoozeCount(int maxSnoozeCount) { this.maxSnoozeCount = maxSnoozeCount; }

    // Utility methods
    public boolean hasRepeatDays() {
        for (boolean day : repeatDays) {
            if (day) return true;
        }
        return false;
    }

    public String getRepeatDaysString() {
        if (!hasRepeatDays()) return "Một lần";

        String[] dayNames = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repeatDays.length; i++) {
            if (repeatDays[i]) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(dayNames[i]);
            }
        }
        return sb.toString();
    }
}
