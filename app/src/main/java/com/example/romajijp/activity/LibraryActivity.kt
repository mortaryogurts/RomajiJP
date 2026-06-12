package com.example.romajijp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.romajijp.R
import com.example.romajijp.adapter.SongAdapter
import com.example.romajijp.databinding.ActivityLibraryBinding
import com.example.romajijp.db.AppDatabase
import com.example.romajijp.model.Song
import kotlinx.coroutines.launch

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding
    private lateinit var adapter: SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_library)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        loadLibrary()
    }

    private fun setupRecyclerView() {
        adapter = SongAdapter(
            onSongClicked = { song ->
                val intent = Intent(this, LyricsDisplay::class.java).apply {
                    putExtra("song_title", song.title)
                    putExtra("song_artist", song.artist)
                    putExtra("song_album", song.album)
                    putExtra("song_artwork", song.artworkUrl)
                    putExtra("song_duration", song.durationMillis)
                    putExtra("song_lyrics", song.lyrics)
                }
                startActivity(intent)
            },
            onSongLongClicked = { song ->
                showDeleteDialog(song)
            }
        )
        binding.libraryRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.libraryRecyclerView.adapter = adapter
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
            // We need a SongCache object to delete it. 
            // We can just query it or create a dummy one with the same cacheId if we only need the PK.
            // But Room @Delete needs the whole object usually, or at least the PK set.
            val songToDelete = db.songDao().getSong(cacheId)
            if (songToDelete != null) {
                db.songDao().deleteSong(songToDelete)
                loadLibrary() // Refresh the list
                android.widget.Toast.makeText(this@LibraryActivity, "Song deleted", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadLibrary() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val savedSongs = db.songDao().getAllSongs()
            
            if (savedSongs.isEmpty()) {
                binding.emptyLibraryText.isVisible = true
                binding.libraryRecyclerView.isVisible = false
            } else {
                binding.emptyLibraryText.isVisible = false
                binding.libraryRecyclerView.isVisible = true
                
                val songs = savedSongs.map { cache ->
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
                adapter.updateSongs(songs)
            }
        }
    }
}