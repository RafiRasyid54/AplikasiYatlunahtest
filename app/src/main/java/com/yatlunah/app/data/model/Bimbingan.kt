package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

// 1. Model Utama (Entity) - Digunakan untuk MEMBACA data
data class Bimbingan(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: String,
    // ✅ TAMBAHAN: nama_santri dikirim oleh Backend hasil JOIN SQL
    @SerializedName("nama_santri") val namaSantri: String? = "Santri",
    @SerializedName("jenis_bimbingan") val jenisBimbingan: String,
    @SerializedName("status") val status: String,
    @SerializedName("tanggal_daftar") val tanggalDaftar: String
)

// 2. Model Request - Digunakan saat Santri MENDAFTAR (POST)
data class BimbinganRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("jenis_bimbingan") val jenisBimbingan: String,
    @SerializedName("status") val status: String = "Menunggu",
    @SerializedName("tanggal_daftar") val tanggalDaftar: String
)

// 3. Model Response dari API
data class BimbinganResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<Bimbingan>? = null,
    @SerializedName("message") val message: String? = null
)

// 4. Model untuk Ubah Status (Terima/Tolak)
data class UpdateStatusRequest(
    @SerializedName("status") val status: String,
    // ✅ WAJIB: Tambahkan id_guru agar Backend tahu siapa yang menerima
    @SerializedName("id_guru") val idGuru: String
)

data class UpdateStatusResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null
)