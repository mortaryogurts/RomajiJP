package com.example.romajijp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.romajijp.activity.ui.theme.BrandMuted
import com.example.romajijp.activity.ui.theme.BrandPeach
import com.example.romajijp.activity.ui.theme.RomajiJPTheme
import com.example.romajijp.model.Song

@Composable
fun SongDetailsHeader(
    song: Song,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.size(200.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = BorderStroke(2.dp, BrandPeach)
        ) {
            AsyncImage(
                model = song.artworkUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = song.title,
            color = BrandPeach,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = song.artist,
            color = BrandMuted,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (!song.album.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = song.album,
                color = BrandMuted.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2D0B1C)
@Composable
fun SongDetailsHeaderPreview() {
    RomajiJPTheme {
        SongDetailsHeader(
            song = Song(
                id = 1,
                title = "Renai Circulation",
                artist = "Kana Hanazawa",
                album = "Utamonogatari",
                lyrics = null,
                artworkUrl = null
            )
        )
    }
}