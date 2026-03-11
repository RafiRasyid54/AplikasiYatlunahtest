package com.yatlunah.app.data.model

data class Materi(
    val id: Int,
    val jilid: Int,
    val halaman: Int,
    val teks: String,
    val audioResId: Int?, // Mengacu pada file di res/raw
    val isLatihan: Boolean // Jika true, tombol audio disembunyikan [cite: 41]
)