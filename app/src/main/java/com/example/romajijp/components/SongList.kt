package com.example.romajijp.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.romajijp.activity.ui.theme.RomajiJPTheme
import com.example.romajijp.model.Song

@Composable
fun SongList(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(songs) { song ->
            SongCard(
                song = song,
                onClick = { onSongClick(song) },
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2D0B1C)
@Composable
fun SongListPreview() {
    val mockSongs = listOf(
        Song(1, "Song 1", "Artist 1", "Album 1", null, null),
        Song(2, "Song 2", "Artist 2", "Album 2", null, null),
        Song(3, "Song 3", "Artist 3", "Album 3", null, null),
        Song(4, "Song 4", "Artist 4", "Album 4", null, null)
    )
    RomajiJPTheme {
        SongList(
            songs = mockSongs,
            onSongClick = {}
        )
    }
}