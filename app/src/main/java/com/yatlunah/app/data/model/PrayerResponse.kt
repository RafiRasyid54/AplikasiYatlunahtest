package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class PrayerResponse(
    val data: PrayerData
)

data class PrayerData(
    val timings: Timings,
    val date: DateInfo
)

data class Timings(
    @SerializedName("Fajr") val fajr: String,
    @SerializedName("Dhuhr") val dhuhr: String,
    @SerializedName("Asr") val asr: String,
    @SerializedName("Maghrib") val maghrib: String,
    @SerializedName("Isha") val isha: String
)

data class DateInfo(
    val hijri: HijriData
)

data class HijriData(
    val date: String,
    val month: HijriMonth,
    val year: String
)

data class HijriMonth(
    val en: String,
    val ar: String
)