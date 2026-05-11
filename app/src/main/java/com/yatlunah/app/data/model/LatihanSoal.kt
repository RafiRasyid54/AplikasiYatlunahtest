package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class LatihanSoal(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("jilid_id") // WAJIB: Harus sama dengan di image_a36e89.png
    val jilidId: Int,

    @SerializedName("halaman_target") // WAJIB: Harus sama dengan di image_a36e89.png
    val halamanTarget: Int,

    @SerializedName("kategori")
    val kategori: String,

    @SerializedName("pertanyaan")
    val pertanyaan: String,

    @SerializedName("pilihan_jawaban") // Supabase jsonb otomatis terbaca sebagai List
    val pilihanJawaban: List<String>,

    @SerializedName("kunci_jawaban")
    val kunciJawaban: String
)