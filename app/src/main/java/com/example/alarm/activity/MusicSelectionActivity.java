package com.example.alarm.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alarm.R;
import com.example.alarm.util.MusicUtils;

import java.util.List;

public class MusicSelectionActivity extends AppCompatActivity {
    
    private ListView musicListView;
    private Button selectButton, cancelButton, previewButton;
    private TextView selectedMusicText;
    
    private List<MusicUtils.MusicItem> musicList;
    private MusicUtils.MusicItem selectedMusic;
    private MediaPlayer previewPlayer;
    private ArrayAdapter<MusicUtils.MusicItem> adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_selection);
        
        initViews();
        loadMusicList();
        setupListeners();
    }
    
    private void initViews() {
        musicListView = findViewById(R.id.musicListView);
        selectButton = findViewById(R.id.selectButton);
        cancelButton = findViewById(R.id.cancelButton);
        previewButton = findViewById(R.id.previewButton);
        selectedMusicText = findViewById(R.id.selectedMusicText);
        
        selectButton.setEnabled(false);
        previewButton.setEnabled(false);
    }
    
    private void loadMusicList() {
        musicList = MusicUtils.getAllMusicFiles(this);
        
        if (musicList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy file nhạc nào", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        adapter = new ArrayAdapter<MusicUtils.MusicItem>(this, 
            android.R.layout.simple_list_item_single_choice, musicList) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                MusicUtils.MusicItem item = musicList.get(position);
                textView.setText(item.toString() + "\n" + MusicUtils.formatDuration(item.duration));
                return view;
            }
        };
        
        musicListView.setAdapter(adapter);
        musicListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }
    
    private void setupListeners() {
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMusic = musicList.get(position);
                selectedMusicText.setText("Đã chọn: " + selectedMusic.toString());
                selectButton.setEnabled(true);
                previewButton.setEnabled(true);
                
                // Stop any current preview
                stopPreview();
            }
        });
        
        selectButton.setOnClickListener(v -> {
            if (selectedMusic != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_music_path", selectedMusic.path);
                resultIntent.putExtra("selected_music_title", selectedMusic.toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        
        cancelButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        
        previewButton.setOnClickListener(v -> {
            if (selectedMusic != null) {
                if (previewPlayer != null && previewPlayer.isPlaying()) {
                    stopPreview();
                    previewButton.setText("Nghe thử");
                } else {
                    playPreview();
                    previewButton.setText("Dừng");
                }
            }
        });
    }
    
    private void playPreview() {
        try {
            stopPreview();
            previewPlayer = new MediaPlayer();
            previewPlayer.setDataSource(selectedMusic.path);
            previewPlayer.prepare();
            previewPlayer.start();
            
            // Auto stop after 30 seconds
            previewPlayer.setOnCompletionListener(mp -> {
                stopPreview();
                previewButton.setText("Nghe thử");
            });
            
        } catch (Exception e) {
            Toast.makeText(this, "Không thể phát nhạc: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            previewButton.setText("Nghe thử");
        }
    }
    
    private void stopPreview() {
        if (previewPlayer != null) {
            try {
                if (previewPlayer.isPlaying()) {
                    previewPlayer.stop();
                }
                previewPlayer.release();
            } catch (Exception e) {
                // Ignore
            }
            previewPlayer = null;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPreview();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        stopPreview();
        previewButton.setText("Nghe thử");
    }
}
