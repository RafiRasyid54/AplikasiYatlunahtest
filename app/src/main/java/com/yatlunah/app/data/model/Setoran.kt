package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class Setoran(
    val id: Int,

    @SerializedName("user_id")
    val userId: String,

    // Sesuai dengan kolom 'nama_lengkap' di tabel setoran baru
    @SerializedName("nama_lengkap")
    val namaSantri: String? = null,

    val jilid: Int,
    val halaman: Int,

    @SerializedName("audio_url")
    val audioUrl: String,

    val status: String, // 'menunggu' atau 'dinilai'

    val nilai: Int? = null,
    val catatan: String? = null,

    @SerializedName("id_guru_penilai")
    val penilaiId: String? = null, // UUID Guru yang menilai

    @SerializedName("created_at")
    val createdAt: String
)