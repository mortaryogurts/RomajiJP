package com.example.romajijp.model

import com.google.gson.annotations.SerializedName


data class ItunesResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results")     val results: List<ItunesTrack>
)