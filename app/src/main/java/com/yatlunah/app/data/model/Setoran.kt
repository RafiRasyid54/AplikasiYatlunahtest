package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class Setoran(
    val id: Int,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("nama_lengkap") // Sesuaikan dengan kolom 'nama_lengkap' di DB ✅
    val namaSantri: String? = null,

    val jilid: Int,
    val halaman: Int,

    @SerializedName("audio_url") // Sesuaikan dengan kolom 'audio_url' di DB ✅
    val audioUrl: String,

    val status: String, // "menunggu" atau "dinilai"
    val nilai: Int? = null,
    val catatan: String? = null,

    @SerializedName("created_at") // Sesuaikan dengan kolom 'created_at' di DB ✅
    val createdAt: String
)