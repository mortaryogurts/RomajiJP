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
import com.ibm.icu.text.Transliterator
import kotlinx.coroutines.launch
import java.time.chrono.JapaneseEra

class LyricsDisplay : AppCompatActivity() {

    private var isJapanese : Boolean = false
    private var originalLyrics : String? = null
    private var romanizedLyrics : String? = null
    private lateinit var transliterator: Transliterator
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

        transliterator = Transliterator.getInstance("Any-Latin; NFD; [:NonSpacing Mark:] Remove; NFC")

        val title = intent.getStringExtra("song_title") ?: ""
        val artist = intent.getStringExtra("song_artist") ?: ""
        val album = intent.getStringExtra("song_album")
        val lyrics = intent.getStringExtra("song_lyrics")
        val artwork = intent.getStringExtra("song_artwork")
        val duration = intent.getLongExtra("song_duration", 0)

        val song = Song(0, title, artist, album, lyrics, artwork, duration)
        binding.song = song
        binding.toolbar.title = title

        setupButtons()

        // If lyrics are missing, fetch them now
        if (lyrics == null) {
            fetchLyrics(song)
        } else {
            processLyrics(song, lyrics)
        }
    }

    private fun setupButtons() {
        binding.btnOriginalLyrics.setOnClickListener {
            binding.lyricsText.text = originalLyrics
        }
        binding.btnRomanizedLyrics.setOnClickListener {
            binding.lyricsText.text = romanizedLyrics
        }
    }

    private fun fetchLyrics(song: Song) {
        lifecycleScope.launch {
            val fetchedLyrics = repository.fetchLyrics(
                song.title,
                song.artist,
                song.album,
                song.durationMillis
            )
            
            if (fetchedLyrics != null) {
                processLyrics(song, fetchedLyrics)
            }
        }
    }

    private fun processLyrics(song: Song, lyrics: String) {
        originalLyrics = lyrics
        isJapanese = containsJapanese(lyrics)
        binding.isJP = isJapanese
        
        if (isJapanese) {
            romanizedLyrics = romanizeWithICU(lyrics)
        }

        song.lyrics = lyrics
        binding.song = song 
        binding.executePendingBindings()
    }
    fun containsJapanese(text: String): Boolean {
        return text.any { char ->
            char.code in 0x3040..0x309F ||  // Hiragana
                    char.code in 0x30A0..0x30FF ||  // Katakana
                    char.code in 0x4E00..0x9FFF     // Kanji
        }
    }

    fun romanizeWithICU(text: String): String {
        return transliterator.transliterate(text)
    }
}