package com.example.romajijp.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.atilika.kuromoji.ipadic.Token
import com.atilika.kuromoji.ipadic.Tokenizer
import com.example.romajijp.R
import com.example.romajijp.databinding.ActivityLyricsDisplayBinding
import com.example.romajijp.model.Song
import com.example.romajijp.repository.MusicRepository
import com.ibm.icu.text.Transliterator
import kotlinx.coroutines.launch
import kotlin.text.append

class LyricsDisplay : AppCompatActivity() {
    private val tokenizer = Tokenizer()
    private var isJapanese : Boolean = false
    private var originalLyrics : String? = null
    private var romanizedLyrics : String? = null
    private lateinit var transliterator: Transliterator
    private lateinit var binding: ActivityLyricsDisplayBinding
    private lateinit var repository: MusicRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lyrics_display)
        repository = MusicRepository(applicationContext)
        
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
            binding.isLoading = false
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

        binding.btnDownload.setOnClickListener {
            binding.song?.let { currentSong ->
                lifecycleScope.launch {
                    repository.downloadSong(currentSong)
                    android.widget.Toast.makeText(
                        this@LyricsDisplay,
                        "Song saved to library",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchLyrics(song: Song) {
        lifecycleScope.launch {
            binding.isLoading = true
            val fetchedLyrics = repository.fetchLyrics(
                song.title,
                song.artist,
                song.album,
                song.durationMillis
            )
            binding.isLoading = false
            
            if (fetchedLyrics != null) {
                processLyrics(song, fetchedLyrics)
            } else {
                processLyrics(song, "Lyrics not found.")
            }
        }
    }

    private fun processLyrics(song: Song, lyrics: String) {
        originalLyrics = lyrics
        isJapanese = containsJapanese(lyrics)
        binding.isJP = isJapanese
        
        if (isJapanese) {
            romanizedLyrics = romanizeWithKuromoji(lyrics)
        }

        song.lyrics = lyrics
        binding.song = song 
        binding.executePendingBindings()
    }
    fun containsJapanese(text: String): Boolean {
        return text.any { char ->
            char.code in 0x3040..0x309F ||  // Hiragana
                    char.code in 0x30A0..0x30FF ||  // Katakana
                    char.code in 0x4E00..0x9FFF ||  // Kanji
                    char.code in 0xFF66..0xFF9F     // Half-width Katakana
        }
    }

    fun romanizeWithKuromoji(text: String): String {
        val tokens: List<Token> = tokenizer.tokenize(text)
        val result = StringBuilder()

        for (token in tokens) {
            val surface = token.surface
            val reading = token.reading
            val pos = token.partOfSpeechLevel1

            if (reading == null || reading == "*" || reading.isBlank()) {
                // Keep original text for punctuation, English, or whitespace
                result.append(surface)
                continue
            }

            var romaji: String
            
            // 1. Correct Particle Pronunciation
            // Particles like は (ha), へ (he), and を (wo) are pronounced wa, e, and o.
            if (pos == "助詞") {
                romaji = when (surface) {
                    "は" -> "wa"
                    "へ" -> "e"
                    "を" -> "o"
                    else -> transliterator.transliterate(reading)
                }
            } else {
                romaji = transliterator.transliterate(reading)
            }

            romaji = romaji.lowercase()

            // 2. Intelligent Spacing
            // Add a space between words if the previous token wasn't a newline or space
            if (result.isNotEmpty() && !result.endsWith("\n") && !result.endsWith(" ")) {
                if (!isPunctuation(surface)) {
                    result.append(" ")
                }
            }

            result.append(romaji)
        }

        // Cleanup: remove extra spaces and fix spacing around newlines
        return result.toString()
            .replace(Regex(" +"), " ")
            .replace(Regex(" \n"), "\n")
            .trim()
    }

    private fun isPunctuation(text: String): Boolean {
        return text.all { it in "、。！？（）「」『』,.;:!?()[]{}<>\"' " || it == '　' || it.isWhitespace() }
    }
}