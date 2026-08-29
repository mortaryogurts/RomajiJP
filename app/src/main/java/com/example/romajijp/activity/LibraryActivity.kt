package com.example.romajijp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import com.example.romajijp.activity.ui.theme.RomajiJPTheme
import com.example.romajijp.db.AppDatabase
import com.example.romajijp.model.Song
import kotlinx.coroutines.launch

class LibraryActivity : ComponentActivity() {

    private var songs = mutableStateOf<List<Song>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        loadLibrary()

        setContent {
            RomajiJPTheme {
                LibraryScreen(
                    songs = songs.value,
                    onSongClick = { song ->
                        val intent = Intent(this, LyricsScreen::class.java).apply {
                            putExtra("song_title", song.title)
                            putExtra("song_artist", song.artist)
                            putExtra("song_album", song.album)
                            putExtra("song_artwork", song.artworkUrl)
                            putExtra("song_duration", song.durationMillis)
                            putExtra("song_lyrics", song.lyrics)
                        }
                        startActivity(intent)
                    },
                    onSongLongClick = { song ->
                        showDeleteDialog(song)
                    },
                    onBackClick = { finish() }
                )
            }
        }
    }

    private fun showDeleteDialog(song: Song) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Song")
            .setMessage("Are you sure you want to delete \"${song.title}\" from your library?")
            .setPositiveButton("Delete") { _, _ ->
                deleteSongFromLibrary(song)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteSongFromLibrary(song: Song) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val cacheId = "${song.title.lowercase()}_${song.artist.lowercase()}"
            val songToDelete = db.songDao().getSong(cacheId)
            if (songToDelete != null) {
                db.songDao().deleteSong(songToDelete)
                loadLibrary() // Refresh the list
                Toast.makeText(this@LibraryActivity, "Song deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadLibrary() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val savedSongs = db.songDao().getAllSongs()
            
            songs.value = savedSongs.map { cache ->
                Song(
                    id = 0,
                    title = cache.title,
                    artist = cache.artist,
                    album = cache.album,
                    lyrics = cache.lyrics,
                    artworkUrl = cache.artworkUrl,
                    durationMillis = cache.durationMillis
                )
            }
        }
    }
}
