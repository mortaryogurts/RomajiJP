package com.example.romajijp.retrofit

import com.example.romajijp.apiclient.ITunesClient
import com.example.romajijp.apiclient.TranslationClient
import com.example.romajijp.apiclient.LrClibClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LrClibRetrofitClient {
    private const val base_url = "https://lrclib.net/api/"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "RomajiJP/1.0 (https://github.com/kanishk-sh0/RomajiJP)")
                .build()
            chain.proceed(request)
        }
        .build()

    val instance : LrClibClient by lazy {
        Retrofit.Builder()
            .baseUrl(base_url)
            .client(client)
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

object TranslationRetrofitClient {
    private const val base_url = "https://api.mymemory.translated.net/"

    val instance : TranslationClient by lazy {
        Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslationClient::class.java)
    }
}