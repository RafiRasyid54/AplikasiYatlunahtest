package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName


data class BimbinganRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("jenis_bimbingan") val jenisBimbingan: String, // "Online" atau "Tatap Muka" [cite: 80, 82]
    @SerializedName("status") val status: String = "Menunggu",
    @SerializedName("tanggal_daftar") val tanggalDaftar: String
)