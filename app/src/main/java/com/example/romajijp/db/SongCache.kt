package com.example.romajijp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_cache")
data class SongCache(
    @PrimaryKey val cacheId: String, // Combination of title and artist
    val title: String,
    val artist: String,
    val album: String?,
    val lyrics: String?,
    val artworkUrl: String?,
    val durationMillis: Long,
    val timestamp: Long = System.currentTimeMillis()
)