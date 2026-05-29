package com.example.romajijp.retrofit

import com.example.romajijp.apiclient.LyricsClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val base_url = "https://lrclib.net/api/"

    val api : LyricsClient by lazy {
        Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LyricsClient::class.java)
    }
}