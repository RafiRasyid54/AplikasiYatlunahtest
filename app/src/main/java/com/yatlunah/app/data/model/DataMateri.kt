package com.yatlunah.app.data.model

data class Materi(
    val id: Int,
    val jilid: Int,
    val halaman: Int,
    val teks: String,
    val audioResId: Int?, // Mengacu pada file di res/raw
    val isLatihan: Boolean // Jika true, tombol audio disembunyikan [cite: 41]
)

// Simpan di data/model/DataMateri.kt
data class JilidItem(
    val id: Int,
    val title: String,
    val status: String,
    val progress: Float,
    val pdfPath: String // Jalur file di assets, misal: "pdf/jilid1.pdf"
)