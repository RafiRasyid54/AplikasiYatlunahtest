package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class JilidData(
    @SerializedName("nomor_jilid")
    val nomorJilid: Int,

    @SerializedName("judul_jilid")
    val judulJilid: String,

    @SerializedName("pdf_url")
    val pdfUrl: String?, // ✅ Pakai tanda tanya (?) karena bisa saja null dari server

    @SerializedName("file_size")
    val fileSize: String?, // ✅ Pakai tanda tanya

    @SerializedName("total_halaman")
    val totalHalaman: Int? = null, // ✅ Tambahkan ini karena di JSON ada

    var isDownloaded: Boolean = false,
    var downloadProgress: Float = 0f
)