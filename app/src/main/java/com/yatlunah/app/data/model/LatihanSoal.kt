package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class LatihanSoal(
    val id: Int? = null,
    @SerializedName("jilid_id") val jilidId: Int = 1,
    @SerializedName("halaman_target") val halamanTarget: Int = 0,
    val kategori: String? = "",
    val pertanyaan: String = "",
    @SerializedName("pilihan_jawaban") val pilihanJawaban: List<String> = emptyList(),
    @SerializedName("kunci_jawaban") val kunciJawaban: String = ""
)