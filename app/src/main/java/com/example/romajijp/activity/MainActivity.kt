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
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.romajijp.R
import com.example.romajijp.adapter.SearchHistoryAdapter
import com.example.romajijp.adapter.SongAdapter
import com.example.romajijp.databinding.ActivityMainBinding
import com.example.romajijp.searchhistorymanager.SearchHistoryManager
import com.example.romajijp.viewmodel.LyricsViewModel

class  MainActivity : AppCompatActivity() {

    private lateinit var historyManager: SearchHistoryManager
    private lateinit var historyAdapter: SearchHistoryAdapter
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val DEBOUNCE_DELAY = 300L // ms
    private lateinit var adapter: SongAdapter
    private lateinit var viewModel: LyricsViewModel
    private lateinit var binding: ActivityMainBinding
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

        viewModel = ViewModelProvider(this).get(LyricsViewModel::class.java)

        viewModel.songLiveData.observe(this) { songs ->
            adapter.updateSongs(songs)
            if (songs.isNotEmpty()) {
                binding.previousSearch = true
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.isLoading = isLoading
        }
        
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
        adapter = SongAdapter()
        binding.songRecyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = this@MainActivity.adapter
        }
        binding.header.setText("Search Results")
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
}
