package com.example.alarm.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alarm.R;

import java.util.ArrayList;
import java.util.Locale;

public class StopwatchActivity extends BaseActivity {

    private TextView timeDisplay;
    private Button startButton, lapButton, resetButton;
    private ListView lapListView;

    private Handler handler = new Handler();
    private long startTime = 0;
    private long elapsedTime = 0;
    private boolean isRunning = false;

    private ArrayList<String> lapTimes;
    private ArrayAdapter<String> lapAdapter;

    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime;
                updateTimeDisplay();
                handler.postDelayed(this, 10); // Update every 10ms for smooth display
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        setupBottomNavigation();
        setSelectedNavigationItem(R.id.menu_stopwatch);

        initViews();
        setupLapList();
        setupListeners();
    }

    private void initViews() {
        timeDisplay = findViewById(R.id.timeDisplay);
        startButton = findViewById(R.id.startButton);
        lapButton = findViewById(R.id.lapButton);
        resetButton = findViewById(R.id.resetButton);
        lapListView = findViewById(R.id.lapListView);

        lapButton.setEnabled(false);
        resetButton.setEnabled(false);

        updateTimeDisplay();
    }

    private void setupLapList() {
        lapTimes = new ArrayList<>();
        lapAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lapTimes);
        lapListView.setAdapter(lapAdapter);
    }

    private void setupListeners() {
        startButton.setOnClickListener(v -> {
            if (isRunning) {
                stopStopwatch();
            } else {
                startStopwatch();
            }
        });

        lapButton.setOnClickListener(v -> recordLap());
        resetButton.setOnClickListener(v -> resetStopwatch());
    }

    private void startStopwatch() {
        if (elapsedTime == 0) {
            startTime = System.currentTimeMillis();
        } else {
            startTime = System.currentTimeMillis() - elapsedTime;
        }

        isRunning = true;
        handler.post(updateTimeRunnable);

        startButton.setText("Dừng");
        lapButton.setEnabled(true);
        resetButton.setEnabled(false);
    }

    private void stopStopwatch() {
        isRunning = false;
        handler.removeCallbacks(updateTimeRunnable);

        startButton.setText("Tiếp tục");
        lapButton.setEnabled(false);
        resetButton.setEnabled(true);
    }

    private void resetStopwatch() {
        isRunning = false;
        elapsedTime = 0;
        handler.removeCallbacks(updateTimeRunnable);

        updateTimeDisplay();
        lapTimes.clear();
        lapAdapter.notifyDataSetChanged();

        startButton.setText("Bắt đầu");
        lapButton.setEnabled(false);
        resetButton.setEnabled(false);
    }

    private void recordLap() {
        if (isRunning) {
            String lapTime = formatTime(elapsedTime);
            int lapNumber = lapTimes.size() + 1;
            lapTimes.add(0, String.format("Vòng %d: %s", lapNumber, lapTime));
            lapAdapter.notifyDataSetChanged();
        }
    }

    private void updateTimeDisplay() {
        timeDisplay.setText(formatTime(elapsedTime));
    }

    private String formatTime(long timeInMillis) {
        int minutes = (int) (timeInMillis / 60000);
        int seconds = (int) (timeInMillis % 60000) / 1000;
        int centiseconds = (int) (timeInMillis % 1000) / 10;

        return String.format(Locale.getDefault(), "%02d:%02d.%02d", minutes, seconds, centiseconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimeRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Keep the stopwatch running in background
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRunning) {
            handler.post(updateTimeRunnable);
        }
    }
}