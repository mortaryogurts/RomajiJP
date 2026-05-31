package com.example.romajijp.retrofit

import com.example.romajijp.apiclient.ITunesClient
import com.example.romajijp.apiclient.LrClibClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import kotlin.io.encoding.Base64

object LrClibRetrofitClient {
    private const val base_url = "https://lrclib.net/api/"

    val instance : LrClibClient by lazy {
        Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LrClibClient :: class.java)
    }

}

object ITunesRetrofitClient {
    private const val base_url = "https://itunes.apple.com/"

    val instance : ITunesClient by lazy {
        Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesClient :: class.java)
    }
}