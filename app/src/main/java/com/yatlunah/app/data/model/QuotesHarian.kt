package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class QuotesHarian(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("teks_quote") // Ini HARUS sama dengan di database Python
    val teksQuote: String,

    @SerializedName("sumber")
    val sumber: String,

    @SerializedName("hari")
    val hari: String? = null
)