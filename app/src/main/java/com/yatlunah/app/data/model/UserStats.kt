package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class UserStats(
    @SerializedName("current_streak")
    val currentStreak: Int,

    @SerializedName("last_jilid")
    val lastJilid: Int,

    @SerializedName("last_halaman")
    val lastHalaman: Int,

    // ✅ Pastikan nama di sini adalah totalProgress
    // agar sinkron dengan yang dipanggil di ViewModel
    @SerializedName("total_progress")
    val totalProgress: Float
)