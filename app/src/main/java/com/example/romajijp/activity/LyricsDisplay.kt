package com.example.romajijp.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.romajijp.R
import com.example.romajijp.databinding.ActivityLyricsDisplayBinding
import com.example.romajijp.model.Song
import com.example.romajijp.repository.MusicRepository
import kotlinx.coroutines.launch

class LyricsDisplay : AppCompatActivity() {
    private lateinit var binding: ActivityLyricsDisplayBinding
    private val repository = MusicRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lyrics_display)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        val title = intent.getStringExtra("song_title") ?: ""
        val artist = intent.getStringExtra("song_artist") ?: ""
        val album = intent.getStringExtra("song_album")
        val lyrics = intent.getStringExtra("song_lyrics")
        val artwork = intent.getStringExtra("song_artwork")
        val duration = intent.getLongExtra("song_duration", 0)

        val song = Song(0, title, artist, album, lyrics, artwork, duration)
        binding.song = song
        binding.toolbar.title = title

        // If lyrics are missing, fetch them now
        if (lyrics == null) {
            fetchLyrics(song)
        }
    }

    private fun fetchLyrics(song: Song) {
        lifecycleScope.launch {
            // Optional: Show a loading state if you have one in your layout
            val fetchedLyrics = repository.fetchLyrics(
                song.title,
                song.artist,
                song.album,
                song.durationMillis
            )
            
            if (fetchedLyrics != null) {
                song.lyrics = fetchedLyrics
                // Update binding
                binding.song = song 
                // Because Song is a data class and we changed a var, 
                // we might need to notify the binding explicitly if not using LiveData
                binding.executePendingBindings()
            }
        }
    }
}