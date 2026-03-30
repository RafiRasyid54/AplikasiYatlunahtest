package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class SetoranPenilaianRequest(
    @SerializedName("setoran_id")
    val setoranId: Int, // Gunakan camelCase di sini

    val nilai: Int,

    val catatan: String,

    @SerializedName("id_guru_penilai")
    val idGuruPenilai: String // Gunakan camelCase di sini
)