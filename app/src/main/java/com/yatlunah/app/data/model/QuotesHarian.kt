package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class QuotesHarian(
    val id: Int,
    @SerializedName("teks_quote")
    val teksQuote: String,
    val sumber: String,
    @SerializedName("tgl_tayang")
    val tglTayang: String
)