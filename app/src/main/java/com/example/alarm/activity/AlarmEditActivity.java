package com.example.alarm.activity;



import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.example.alarm.R;
import com.example.alarm.database.AlarmDatabaseHelper;
import com.example.alarm.model.Alarm;
import com.example.alarm.util.AlarmUtils;
import com.example.alarm.util.MusicUtils;

import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.Calendar;

public class AlarmEditActivity extends AppCompatActivity {

    private TextView timeTextView, volumeValueText, selectedMusicText, autoSnoozeText, autoDismissText;
    private EditText labelEditText;
    private Button saveBtn, ringtoneBtn, favoriteMusicBtn;
    private CheckBox[] dayCheckBoxes = new CheckBox[7];
    private SeekBar volumeSeek, autoSnoozeSeek, autoDismissSeek;
    private Switch loopSwitch, ignoreSwitch, mathSwitch, randomMusicSwitch;
    private Switch vibrateSwitch, gradualVolumeSwitch, upcomingNotificationSwitch;
    private Spinner mathDifficultySpinner;
    private Alarm alarm;
    private AlarmDatabaseHelper db;

    private final int RINGTONE_REQUEST = 101;
    private final int MUSIC_SELECTION_REQUEST = 102;
    private Uri ringtoneUri = null;
    private String selectedMusicPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit);

        timeTextView = findViewById(R.id.timeTextView);
        labelEditText = findViewById(R.id.labelEditText);
        volumeValueText = findViewById(R.id.volumeText);
        selectedMusicText = findViewById(R.id.selectedMusicText);
        autoSnoozeText = findViewById(R.id.autoSnoozeText);
        autoDismissText = findViewById(R.id.autoDismissText);

        saveBtn = findViewById(R.id.saveButton);
        ringtoneBtn = findViewById(R.id.ringtoneButton);
        favoriteMusicBtn = findViewById(R.id.favoriteMusicButton);

        volumeSeek = findViewById(R.id.volumeSeekBar);
        autoSnoozeSeek = findViewById(R.id.autoSnoozeSeekBar);
        autoDismissSeek = findViewById(R.id.autoDismissSeekBar);

        loopSwitch = findViewById(R.id.loopSwitch);
        ignoreSwitch = findViewById(R.id.ignoreSwitch);
        mathSwitch = findViewById(R.id.mathSwitch);
        randomMusicSwitch = findViewById(R.id.randomSwitch);
        vibrateSwitch = findViewById(R.id.vibrateSwitch);
        gradualVolumeSwitch = findViewById(R.id.gradualVolumeSwitch);
        upcomingNotificationSwitch = findViewById(R.id.upcomingNotificationSwitch);

        mathDifficultySpinner = findViewById(R.id.mathDifficultySpinner);

        dayCheckBoxes[0] = findViewById(R.id.sun);
        dayCheckBoxes[1] = findViewById(R.id.mon);
        dayCheckBoxes[2] = findViewById(R.id.tue);
        dayCheckBoxes[3] = findViewById(R.id.wed);
        dayCheckBoxes[4] = findViewById(R.id.thu);
        dayCheckBoxes[5] = findViewById(R.id.fri);
        dayCheckBoxes[6] = findViewById(R.id.sat);

        db = new AlarmDatabaseHelper(this);

        // Setup math difficulty spinner
        setupMathDifficultySpinner();

        int id = getIntent().getIntExtra("alarm_id", -1);
        if (id != -1) {
            alarm = db.getAlarmById(id);
            if (alarm == null) {
                alarm = new Alarm();
                setDefaultRingtone();
            }
        } else {
            alarm = new Alarm();
            setDefaultRingtone();
        }

        if (alarm != null) {
            updateUIFromAlarm(alarm);
        }

        setupListeners();

    }

    private void setDefaultRingtone() {
        // Set default alarm ringtone
        Uri defaultRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (defaultRingtone != null) {
            alarm.setRingtonePath(defaultRingtone.toString());
        } else {
            // Fallback to notification sound if no alarm sound available
            Uri notificationRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (notificationRingtone != null) {
                alarm.setRingtonePath(notificationRingtone.toString());
            }
        }
    }

    private void setupMathDifficultySpinner() {
        String[] difficulties = {"Dễ", "Trung bình", "Khó"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, difficulties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mathDifficultySpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        timeTextView.setOnClickListener(v -> pickTime());
        ringtoneBtn.setOnClickListener(v -> chooseRingtone());
        favoriteMusicBtn.setOnClickListener(v -> chooseFavoriteMusic());
        saveBtn.setOnClickListener(v -> saveAlarm());

        // Volume SeekBar
        volumeSeek.setMax(100);
        volumeSeek.setProgress(alarm.getVolume());
        volumeValueText.setText(alarm.getVolume() + "%");
        volumeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                alarm.setVolume(i);
                volumeValueText.setText(i + "%");
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Auto Snooze SeekBar
        autoSnoozeSeek.setMax(30);
        autoSnoozeSeek.setProgress(alarm.getAutoSnoozeMinutes());
        autoSnoozeText.setText(alarm.getAutoSnoozeMinutes() + " phút");
        autoSnoozeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                alarm.setAutoSnoozeMinutes(i);
                autoSnoozeText.setText(i + " phút");
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Auto Dismiss SeekBar
        autoDismissSeek.setMax(60);
        autoDismissSeek.setProgress(alarm.getAutoDismissMinutes());
        autoDismissText.setText(alarm.getAutoDismissMinutes() + " phút");
        autoDismissSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                alarm.setAutoDismissMinutes(i);
                autoDismissText.setText(i + " phút");
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void pickTime() {
        TimePickerDialog dialog = new TimePickerDialog(this,
                (TimePicker view, int hourOfDay, int minute) -> {
                    alarm.setHour(hourOfDay);
                    alarm.setMinute(minute);
                    timeTextView.setText(String.format("%02d:%02d", hourOfDay, minute));
                },
                alarm.getHour(), alarm.getMinute(), true);
        dialog.show();
    }

    private void chooseRingtone() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                RingtoneManager.TYPE_ALARM);
        startActivityForResult(intent, RINGTONE_REQUEST);
    }

    private void chooseFavoriteMusic() {
        Intent intent = new Intent(this, MusicSelectionActivity.class);
        startActivityForResult(intent, MUSIC_SELECTION_REQUEST);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == RINGTONE_REQUEST && resultCode == RESULT_OK) {
            ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtoneUri != null) {
                alarm.setRingtonePath(ringtoneUri.toString());
                selectedMusicText.setText("Nhạc chuông: " + MusicUtils.getRingtoneTitle(this, ringtoneUri));
            }
        } else if (reqCode == MUSIC_SELECTION_REQUEST && resultCode == RESULT_OK) {
            selectedMusicPath = data.getStringExtra("selected_music_path");
            String musicTitle = data.getStringExtra("selected_music_title");
            if (selectedMusicPath != null) {
                alarm.setFavoriteMusicPath(selectedMusicPath);
                selectedMusicText.setText("Nhạc yêu thích: " + musicTitle);
            }
        }
    }

    private void saveAlarm() {
        alarm.setLabel(labelEditText.getText().toString());
        alarm.setLoop(loopSwitch.isChecked());
        alarm.setIgnoreAfterRing(ignoreSwitch.isChecked());
        alarm.setRandomMusic(randomMusicSwitch.isChecked());
        alarm.setRequireMathToDismiss(mathSwitch.isChecked());

        // Set new fields
        alarm.setVibrate(vibrateSwitch.isChecked());
        alarm.setGradualVolumeIncrease(gradualVolumeSwitch.isChecked());
        alarm.setShowUpcomingNotification(upcomingNotificationSwitch.isChecked());
        alarm.setMathDifficulty(mathDifficultySpinner.getSelectedItemPosition() + 1);

        boolean[] days = new boolean[7];
        for (int i = 0; i < 7; i++) {
            days[i] = dayCheckBoxes[i].isChecked();
        }
        alarm.setRepeatDays(days);

        if (getIntent().getIntExtra("alarm_id", -1) == -1) {
            // New alarm
            long newId = db.insertAlarm(alarm);
            alarm.setId((int) newId);
        } else {
            // Update existing alarm
            db.updateAlarm(alarm);
        }

        // 👉 Kiểm tra quyền đặt báo thức chính xác trên Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                showRequestAlarmPermissionDialog(); // ← gọi dialog
                return; // Không gọi scheduleAlarm nếu chưa có quyền
            }
        }

        AlarmUtils.scheduleAlarm(this, alarm); // OK, đã có quyền
        finish();
    }


    private void updateUIFromAlarm(Alarm alarm) {
        timeTextView.setText(String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));
        labelEditText.setText(alarm.getLabel());
        loopSwitch.setChecked(alarm.isLoop());
        ignoreSwitch.setChecked(alarm.isIgnoreAfterRing());
        mathSwitch.setChecked(alarm.isRequireMathToDismiss());
        randomMusicSwitch.setChecked(alarm.isRandomMusic());

        // Update new switches
        vibrateSwitch.setChecked(alarm.isVibrate());
        gradualVolumeSwitch.setChecked(alarm.isGradualVolumeIncrease());
        upcomingNotificationSwitch.setChecked(alarm.isShowUpcomingNotification());

        // Update spinner
        mathDifficultySpinner.setSelection(alarm.getMathDifficulty() - 1);

        // Update music selection display
        if (alarm.getFavoriteMusicPath() != null && !alarm.getFavoriteMusicPath().isEmpty()) {
            selectedMusicText.setText("Nhạc yêu thích: " + alarm.getFavoriteMusicPath());
        } else if (alarm.getRingtonePath() != null && !alarm.getRingtonePath().isEmpty()) {
            selectedMusicText.setText("Nhạc chuông: " + alarm.getRingtonePath());
        } else {
            selectedMusicText.setText("Chưa chọn nhạc");
        }

        boolean[] days = alarm.getRepeatDays();
        for (int i = 0; i < 7; i++) {
            dayCheckBoxes[i].setChecked(days[i]);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void showRequestAlarmPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu quyền báo thức chính xác")
                .setMessage("Ứng dụng cần quyền đặt báo thức chính xác để hoạt động đúng. Bạn có muốn cấp quyền không?")
                .setPositiveButton("Cấp quyền", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(intent); // Sau khi quay lại, bạn nên cho người dùng nhấn 'Lưu' lại
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
