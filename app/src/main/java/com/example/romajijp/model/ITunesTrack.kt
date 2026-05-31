package com.example.romajijp.model

import com.google.gson.annotations.SerializedName


data class ItunesTrack(
    @SerializedName("trackName")       val trackName: String,
    @SerializedName("artistName")      val artistName: String,
    @SerializedName("collectionName")  val albumName: String,
    @SerializedName("artworkUrl100")   val artworkUrl100: String?,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long
) {
    // Replace size in URL for better quality — iTunes supports up to 1200x1200
    fun getArtworkUrl(size: Int = 512): String? =
        artworkUrl100?.replace("100x100bb", "${size}x${size}bb")
}