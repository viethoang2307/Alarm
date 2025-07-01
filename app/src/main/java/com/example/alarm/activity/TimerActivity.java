package com.example.alarm.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alarm.R;

public class TimerActivity extends BaseActivity {

    private TextView timerDisplay;
    private NumberPicker hourPicker, minutePicker, secondPicker;
    private Button startButton, pauseButton, resetButton;
    private Button preset5min, preset10min, preset15min, preset30min;

    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;
    private long timeLeftInMillis = 0;
    private boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        setupBottomNavigation();
        setSelectedNavigationItem(R.id.menu_timer);

        initViews();
        setupNumberPickers();
        setupListeners();
    }

    private void initViews() {
        timerDisplay = findViewById(R.id.timerDisplay);
        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);
        secondPicker = findViewById(R.id.secondPicker);

        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);

        preset5min = findViewById(R.id.preset5min);
        preset10min = findViewById(R.id.preset10min);
        preset15min = findViewById(R.id.preset15min);
        preset30min = findViewById(R.id.preset30min);

        pauseButton.setEnabled(false);
        resetButton.setEnabled(false);
    }

    private void setupNumberPickers() {
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(0);

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(5);

        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);
        secondPicker.setValue(0);

        updateTimerDisplay();
    }

    private void setupListeners() {
        startButton.setOnClickListener(v -> startTimer());
        pauseButton.setOnClickListener(v -> pauseTimer());
        resetButton.setOnClickListener(v -> resetTimer());

        preset5min.setOnClickListener(v -> setPresetTime(0, 5, 0));
        preset10min.setOnClickListener(v -> setPresetTime(0, 10, 0));
        preset15min.setOnClickListener(v -> setPresetTime(0, 15, 0));
        preset30min.setOnClickListener(v -> setPresetTime(0, 30, 0));

        NumberPicker.OnValueChangeListener valueChangeListener = (picker, oldVal, newVal) -> {
            if (!isTimerRunning) {
                updateTimerDisplay();
            }
        };

        hourPicker.setOnValueChangedListener(valueChangeListener);
        minutePicker.setOnValueChangedListener(valueChangeListener);
        secondPicker.setOnValueChangedListener(valueChangeListener);
    }

    private void setPresetTime(int hours, int minutes, int seconds) {
        if (!isTimerRunning) {
            hourPicker.setValue(hours);
            minutePicker.setValue(minutes);
            secondPicker.setValue(seconds);
            updateTimerDisplay();
        }
    }

    private void updateTimerDisplay() {
        if (!isTimerRunning) {
            int hours = hourPicker.getValue();
            int minutes = minutePicker.getValue();
            int seconds = secondPicker.getValue();
            timeLeftInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000;
        }

        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerDisplay.setText(timeFormatted);
    }

    private void startTimer() {
        if (timeLeftInMillis <= 0) {
            updateTimerDisplay();
            if (timeLeftInMillis <= 0) {
                Toast.makeText(this, "Vui lòng đặt thời gian", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerDisplay();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                timeLeftInMillis = 0;
                updateTimerDisplay();
                playAlarmSound();

                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
                resetButton.setEnabled(false);

                Toast.makeText(TimerActivity.this, "Hết giờ!", Toast.LENGTH_LONG).show();
            }
        }.start();

        isTimerRunning = true;
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        resetButton.setEnabled(true);
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;

        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(true);
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        isTimerRunning = false;
        timeLeftInMillis = 0;
        updateTimerDisplay();

        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(false);

        stopAlarmSound();
    }

    private void playAlarmSound() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.default_alarm);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAlarmSound() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Exception e) {
                // Ignore
            }
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        stopAlarmSound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAlarmSound();
    }
}