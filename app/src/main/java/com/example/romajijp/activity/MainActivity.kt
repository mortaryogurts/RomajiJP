package com.example.romajijp.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.romajijp.R
import kotlinx.coroutines.launch
import com.example.romajijp.adapter.SearchHistoryAdapter
import com.example.romajijp.adapter.SongAdapter
import com.example.romajijp.databinding.ActivityMainBinding
import com.example.romajijp.searchhistorymanager.SearchHistoryManager
import com.example.romajijp.uistate.MusicUiState
import com.example.romajijp.viewmodel.MusicViewModel
import com.example.romajijp.model.Song
import com.example.romajijp.repository.MusicRepository
import android.content.Intent

class  MainActivity : AppCompatActivity() {

    private lateinit var historyManager: SearchHistoryManager
    private lateinit var historyAdapter: SearchHistoryAdapter
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val DEBOUNCE_DELAY = 300L // ms
    private lateinit var adapter: SongAdapter
    private lateinit var viewModel: MusicViewModel
    private lateinit var binding: ActivityMainBinding
    private val repository = MusicRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        historyManager = SearchHistoryManager(this)
        setUpHistoryRecyclerView()
        setUpRecyclerView()

        viewModel = ViewModelProvider(this).get(MusicViewModel::class.java)
        observeUiState()


        
        binding.getSong.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH){
                val query = binding.getSong.text.toString().trim()
                if (query.isNotEmpty()){
                    historyManager.saveQuery(query)
                    historyAdapter.updateHistory(historyManager.getQueries())
                    performSearch(query)
                }
                true
            }else false
        }


    }

    private fun setUpHistoryRecyclerView() {
        historyAdapter = SearchHistoryAdapter(historyManager.getQueries()) { selectedQuery ->
            binding.getSong.setText(selectedQuery)
            // Move cursor to end
            binding.getSong.setSelection(selectedQuery.length)
            performSearch(selectedQuery)
        }
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = historyAdapter
        }
    }

    private fun setUpRecyclerView() {
        adapter = SongAdapter { song ->
            onSongClicked(song)
        }
        binding.songRecyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = this@MainActivity.adapter
        }
        binding.header.setText("Search Results")
    }

    private fun onSongClicked(song: Song) {
        lifecycleScope.launch {
            binding.isLoading = true
            val lyrics = repository.fetchLyrics(
                song.title,
                song.artist,
                song.album,
                song.durationMillis
            )
            binding.isLoading = false
            
            val intent = Intent(this@MainActivity, LyricsDisplay::class.java).apply {
                putExtra("song_title", song.title)
                putExtra("song_artist", song.artist)
                putExtra("song_album", song.album)
                putExtra("song_lyrics", lyrics)
                putExtra("song_artwork", song.artworkUrl)
                putExtra("song_duration", song.durationMillis)
            }
            startActivity(intent)
        }
    }

    private fun performSearch(query: String) {
        if (query.isNotBlank()) {
            historyManager.saveQuery(query)
            historyAdapter.updateHistory(historyManager.getQueries())
            viewModel.searchSongs(query)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let { searchHandler.removeCallbacks(it) } // prevent leaks
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MusicUiState.Idle    -> {
                            binding.isLoading = false
                            binding.previousSearch = false
                        }
                        is MusicUiState.Loading -> {
                            binding.isLoading = true
                            binding.previousSearch = true
                        }
                        is MusicUiState.Success -> {
                            binding.isLoading = false
                            binding.previousSearch = true
                            adapter.updateSongs(state.songs)
                        }
                        is MusicUiState.Error -> {
                            binding.isLoading = false
                            binding.previousSearch = false
                            Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

}
