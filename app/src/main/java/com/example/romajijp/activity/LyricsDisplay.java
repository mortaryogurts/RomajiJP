package com.example.romajijp.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.romajijp.R;
import com.example.romajijp.databinding.ActivityLyricsDisplayBinding;
import com.example.romajijp.model.Song;

public class LyricsDisplay extends AppCompatActivity {
    private ActivityLyricsDisplayBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lyrics_display);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        String title = getIntent().getStringExtra("song_title");
        String artist = getIntent().getStringExtra("song_artist");
        String album = getIntent().getStringExtra("song_album");
        String lyrics = getIntent().getStringExtra("song_lyrics");


        if (title != null && artist != null) {
            Song song = new Song(0, title, artist, album, lyrics);
            binding.setSong(song);
            binding.toolbar.setTitle(title);
        }
    }
}