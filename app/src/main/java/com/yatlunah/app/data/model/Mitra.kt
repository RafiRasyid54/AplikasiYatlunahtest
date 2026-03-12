package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class Mitra(
    val id: String, // UUID
    @SerializedName("nama_lembaga") val namaLembaga: String,
    val kota: String?,
    val paket: String?,
    @SerializedName("status_sertifikasi") val isCertified: Boolean
)