package com.example.romajijp.model

import com.google.gson.annotations.SerializedName

data class MyMemoryResponse(
    @SerializedName("responseData") val responseData: TranslationData
)

data class TranslationData(
    @SerializedName("translatedText") val translatedText: String
)
