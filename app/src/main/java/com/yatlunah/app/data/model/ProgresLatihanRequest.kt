package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class ProgresLatihanRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("jilid_id") val jilidId: Int,
    @SerializedName("halaman_latihan") val halamanLatihan: Int,
    val skor: Int
)