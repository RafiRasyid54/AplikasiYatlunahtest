package com.yatlunah.app.data.model

data class AdminStatsResponse(
    val totalUser: Int,
    val totalGuru: Int,
    val totalSantri: Int,
    val totalMitra: Int // ✅ TAMBAHKAN INI
)