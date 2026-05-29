package com.example.romajijp.model

import android.R.attr.id

fun SongResponse.toSong(): Song {

    return Song(
        id = id,
        title = trackName,
        artist = artistName,
        album = albumName,
        lyrics = plainLyrics
    )
}