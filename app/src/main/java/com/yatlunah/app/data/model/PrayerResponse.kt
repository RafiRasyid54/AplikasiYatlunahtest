package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class PrayerResponse(
    val data: PrayerData
)

data class PrayerData(
    val timings: Map<String, String>,
    val date: DateData
)

data class DateData(
    val hijri: HijriData
)

data class HijriData(
    val day: String,
    val month: HijriMonth,
    val year: String,
    @SerializedName("format") val designation: Designation
)

data class HijriMonth(
    val en: String, // Nama bulan dalam Inggris (misal: Ramadan)
    val ar: String  // Nama bulan dalam Arab
)

data class Designation(
    val abbreviated: String // Misal: AH
)