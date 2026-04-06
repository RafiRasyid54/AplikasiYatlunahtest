package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class ProgramYatlunah(
    @SerializedName("id") val id: Int,
    @SerializedName("nama") val nama: String,
    @SerializedName("deskripsi") val deskripsi: String,
    @SerializedName("target_peserta") val targetPeserta: String, // Tambahkan ini
    @SerializedName("materi_utama") val materiUtama: String,      // Tambahkan ini
    @SerializedName("fitur_unggulan") val fiturUnggulan: List<String>, // Tambahkan ini
    @SerializedName("icon_res") val iconRes: Int = 0
)