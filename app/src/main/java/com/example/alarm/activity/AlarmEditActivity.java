package com.example.alarm.activity;



import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;


import com.example.alarm.R;
import com.example.alarm.database.AlarmDatabaseHelper;
import com.example.alarm.model.Alarm;
import com.example.alarm.util.AlarmUtils;

import java.util.Calendar;

public class AlarmEditActivity extends AppCompatActivity {

    private TextView timeTextView, labelText, volumeValueText;
    private Button saveBtn, ringtoneBtn;
    private CheckBox[] dayCheckBoxes = new CheckBox[7];
    private SeekBar volumeSeek;
    private Switch loopSwitch, ignoreSwitch, mathSwitch, randomMusicSwitch;
    private Alarm alarm;
    private AlarmDatabaseHelper db;

    private final int RINGTONE_REQUEST = 101;
    private Uri ringtoneUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit);

        timeTextView = findViewById(R.id.timeTextView);
        labelText = findViewById(R.id.labelEditText);
        volumeValueText = findViewById(R.id.volumeText);
        saveBtn = findViewById(R.id.saveButton);
        ringtoneBtn = findViewById(R.id.ringtoneButton);
        volumeSeek = findViewById(R.id.volumeSeekBar);
        loopSwitch = findViewById(R.id.loopSwitch);
        ignoreSwitch = findViewById(R.id.ignoreSwitch);
        mathSwitch = findViewById(R.id.mathSwitch);
        randomMusicSwitch = findViewById(R.id.randomSwitch);

        dayCheckBoxes[0] = findViewById(R.id.sun);
        dayCheckBoxes[1] = findViewById(R.id.mon);
        dayCheckBoxes[2] = findViewById(R.id.tue);
        dayCheckBoxes[3] = findViewById(R.id.wed);
        dayCheckBoxes[4] = findViewById(R.id.thu);
        dayCheckBoxes[5] = findViewById(R.id.fri);
        dayCheckBoxes[6] = findViewById(R.id.sat);

        db = new AlarmDatabaseHelper(this);

        int id = getIntent().getIntExtra("alarm_id", -1);
        alarm = (id != -1) ? db.getAlarmById(id) : new Alarm();

        if (alarm != null) {
            updateUIFromAlarm(alarm);
        }

        timeTextView.setOnClickListener(v -> pickTime());
        ringtoneBtn.setOnClickListener(v -> chooseRingtone());

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

        saveBtn.setOnClickListener(v -> saveAlarm());
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

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == RINGTONE_REQUEST && resultCode == RESULT_OK) {
            ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtoneUri != null) {
                alarm.setRingtonePath(ringtoneUri.toString());
            }
        }
    }

    private void saveAlarm() {
        alarm.setLabel(labelText.getText().toString());
        alarm.setLoop(loopSwitch.isChecked());
        alarm.setIgnoreAfterRing(ignoreSwitch.isChecked());
        alarm.setRandomMusic(randomMusicSwitch.isChecked());
        alarm.setRequireMathToDismiss(mathSwitch.isChecked());

        boolean[] days = new boolean[7];
        for (int i = 0; i < 7; i++) {
            days[i] = dayCheckBoxes[i].isChecked();
        }
        alarm.setRepeatDays(days);

        if (alarm.getId() == 0) {
            db.insertAlarm(alarm);
        } else {
            db.updateAlarm(alarm);
        }

        AlarmUtils.scheduleAlarm(this, alarm); // Set AlarmManager
        finish();
    }

    private void updateUIFromAlarm(Alarm alarm) {
        timeTextView.setText(String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));
        labelText.setText(alarm.getLabel());
        loopSwitch.setChecked(alarm.isLoop());
        ignoreSwitch.setChecked(alarm.isIgnoreAfterRing());
        mathSwitch.setChecked(alarm.isRequireMathToDismiss());
        randomMusicSwitch.setChecked(alarm.isRandomMusic());

        boolean[] days = alarm.getRepeatDays();
        for (int i = 0; i < 7; i++) {
            dayCheckBoxes[i].setChecked(days[i]);
        }
    }
}
