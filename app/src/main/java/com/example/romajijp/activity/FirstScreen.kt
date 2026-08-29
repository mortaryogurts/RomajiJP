package com.example.romajijp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.romajijp.activity.ui.theme.BrandDarker
import com.example.romajijp.activity.ui.theme.BrandPeach
import com.example.romajijp.activity.ui.theme.RomajiJPTheme
import com.example.romajijp.components.EmptyState
import com.example.romajijp.components.RecentlySearchItemsSheet
import com.example.romajijp.components.RomajiSearchBar
import com.example.romajijp.components.SongList
import com.example.romajijp.components.WelcomeState
import com.example.romajijp.model.Song
import com.example.romajijp.searchhistorymanager.SearchHistoryManager
import com.example.romajijp.uistate.MusicUiState
import com.example.romajijp.viewmodel.MusicViewModel

class FirstScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RomajiJPTheme {
                MainScreen(
                    onLibraryClick = {
                        startActivity(Intent(this, LibraryActivity::class.java))
                    },
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
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MusicViewModel = viewModel(),
    onLibraryClick: () -> Unit,
    onSongClick: (Song) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainScreenContent(
        uiState = uiState,
        onSearchSongs = { viewModel.searchSongs(it) },
        onResetState = { viewModel.resetState() },
        onLibraryClick = onLibraryClick,
        onSongClick = onSongClick
    )
}

@Composable
fun MainScreenContent(
    uiState: MusicUiState,
    onSearchSongs: (String) -> Unit,
    onResetState: () -> Unit,
    onLibraryClick: () -> Unit,
    onSongClick: (Song) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val historyManager = remember { SearchHistoryManager(context) }

    var searchQuery by remember { mutableStateOf("") }
    var isHistoryVisible by remember { mutableStateOf(false) }
    var searchHistory by remember { mutableStateOf(historyManager.getQueries()) }


    BackHandler(enabled = isHistoryVisible || uiState !is MusicUiState.Idle) {
        if (isHistoryVisible) {
            isHistoryVisible = false
            focusManager.clearFocus()
        } else {
            onResetState()
            searchQuery = ""
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandDarker)
            .clickable {
                isHistoryVisible = false
                focusManager.clearFocus()
            }) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Dropdown History Sheet (Placed first to be behind the Search Bar)
                if (isHistoryVisible && searchHistory.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp)
                    ) {
                        RecentlySearchItemsSheet(
                            queries = searchHistory,
                            onQueryClick = { query ->
                                searchQuery = query
                                onSearchSongs(query)
                                isHistoryVisible = false
                                focusManager.clearFocus()
                            },
                            onDeleteClick = { query ->
                                historyManager.deleteQuery(query)
                                searchHistory = historyManager.getQueries()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.247.dp)
                                .padding(top = 60.dp) // Space for search bar height
                                .offset(y = (-28).dp) // Tucked under look
                        )
                        
                        // Space to match the width of the Home and Library buttons
                        Spacer(modifier = Modifier.width(96.dp))
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Top Search Bar
                    RomajiSearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            isHistoryVisible = true
                        },
                        onSearch = { query ->
                            if (query.isNotBlank()) {
                                onSearchSongs(query)
                                historyManager.saveQuery(query)
                                searchHistory = historyManager.getQueries()
                                isHistoryVisible = false
                                focusManager.clearFocus()
                            }
                        },
                        onFocusChange = { isFocused ->
                            if (isFocused) {
                                isHistoryVisible = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )

                    IconButton(onClick = {
                        onResetState()
                        searchQuery = ""
                    }) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = BrandPeach)
                    }

                    IconButton(onClick = onLibraryClick) {
                        Icon(
                            Icons.Default.LibraryMusic,
                            contentDescription = "Library",
                            tint = BrandPeach
                        )
                    }
                }
            }
            
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val state = uiState) {
                    is MusicUiState.Idle -> {
                        WelcomeState()

                    }
                    is MusicUiState.Loading -> Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandPeach)
                    }

                    is MusicUiState.Success -> SongList(
                        songs = state.songs,
                        onSongClick = onSongClick
                    )

                    is MusicUiState.Empty -> EmptyState()
                    is MusicUiState.Error -> Text(
                        text = state.message,
                        color = BrandPeach,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    RomajiJPTheme {
        MainScreenContent(
            uiState = MusicUiState.Idle,
            onSearchSongs = {},
            onResetState = {},
            onLibraryClick = {},
            onSongClick = {}
        )
    }
}
