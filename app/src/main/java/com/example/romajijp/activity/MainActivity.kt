package com.example.romajijp.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import android.widget.Toast
import androidx.lifecycle.Lifecycle
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

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback

class  MainActivity : AppCompatActivity() {

    private lateinit var historyManager: SearchHistoryManager
    private lateinit var historyAdapter: SearchHistoryAdapter
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val DEBOUNCE_DELAY = 300L // ms
    private lateinit var adapter: SongAdapter
    private val viewModel: MusicViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var lastClickTime: Long = 0
    private val CLICK_INTERVAL = 500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, ime.bottom)
            )
            insets
        }
        historyManager = SearchHistoryManager(this)
        setUpHistoryRecyclerView()
        setUpRecyclerView()

        observeUiState()

        onBackPressedDispatcher.addCallback(this) {
            if (binding.topSheetCard.visibility == android.view.View.VISIBLE) {
                binding.getSong.clearFocus()
                val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(binding.getSong.windowToken, 0)
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        }

        binding.btnLibrary.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
        }

        binding.main.setOnClickListener {
            binding.getSong.clearFocus()
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.getSong.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showTopSheet()
            } else {
                hideTopSheet()
            }
        }

        binding.getSong.setOnClickListener {
            showTopSheet()
        }

        binding.getSong.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH){
                val query = binding.getSong.text.toString().trim()
                if (query.isNotEmpty()){
                    historyManager.saveQuery(query)
                    historyAdapter.updateHistory(historyManager.getQueries())
                    performSearch(query)
                }
                hideTopSheet()
                binding.getSong.clearFocus()
                true
            }else false
        }
    }

    private fun showTopSheet() {
        val history = historyManager.getQueries()
        if (history.isNotEmpty()) {
            historyAdapter.updateHistory(history)
            binding.topSheetCard.visibility = android.view.View.VISIBLE
        }
    }

    private fun hideTopSheet() {
        binding.topSheetCard.visibility = android.view.View.GONE
    }

    private fun setUpHistoryRecyclerView() {
        historyAdapter = SearchHistoryAdapter(
            history = historyManager.getQueries(),
            onQueryClick = { selectedQuery ->
                binding.getSong.setText(selectedQuery)
                binding.getSong.setSelection(selectedQuery.length)
                performSearch(selectedQuery)
                hideTopSheet()
                binding.getSong.clearFocus()
            },
            onDeleteClick = { queryToDelete ->
                historyManager.deleteQuery(queryToDelete)
                val updatedHistory = historyManager.getQueries()
                historyAdapter.updateHistory(updatedHistory)
                if (updatedHistory.isEmpty()) {
                    hideTopSheet()
                }
            }
        )
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = historyAdapter
        }
    }

    private fun setUpRecyclerView() {
        adapter = SongAdapter(onSongClicked = { song ->
            onSongClicked(song)
        })
        binding.songRecyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = this@MainActivity.adapter
        }
    }

    private fun onSongClicked(song: Song) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < CLICK_INTERVAL) return
        lastClickTime = currentTime

        val intent = Intent(this@MainActivity, LyricsDisplay::class.java).apply {
            putExtra("song_title", song.title)
            putExtra("song_artist", song.artist)
            putExtra("song_album", song.album)
            putExtra("song_artwork", song.artworkUrl)
            putExtra("song_duration", song.durationMillis)
        }
        startActivity(intent)
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
                            binding.isEmpty = false
                        }
                        is MusicUiState.Loading -> {
                            binding.isLoading = true
                            binding.previousSearch = true
                            binding.isEmpty = false
                        }
                        is MusicUiState.Empty -> {
                            binding.isLoading = false
                            binding.previousSearch = true
                            binding.isEmpty = true
                        }
                        is MusicUiState.Success -> {
                            binding.isLoading = false
                            binding.previousSearch = true
                            binding.isEmpty = false
                            adapter.updateSongs(state.songs)
                        }
                        is MusicUiState.Error -> {
                            binding.isLoading = false
                            binding.previousSearch = false
                            binding.isEmpty = false
                            Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is android.widget.EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                val sheetRect = Rect()
                binding.topSheetCard.getGlobalVisibleRect(sheetRect)

                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt()) &&
                    (binding.topSheetCard.visibility != View.VISIBLE || !sheetRect.contains(ev.rawX.toInt(), ev.rawY.toInt()))
                ) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
