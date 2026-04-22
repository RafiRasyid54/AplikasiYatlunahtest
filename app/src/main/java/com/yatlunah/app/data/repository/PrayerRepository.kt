package com.yatlunah.app.data.repository

import com.yatlunah.app.data.model.PrayerResponse
import com.yatlunah.app.data.remote.RetrofitClient
import retrofit2.Response

class PrayerRepository {
    private val apiService = RetrofitClient.prayerApi // Pastikan sudah didaftarkan di RetrofitClient

    suspend fun getPrayerTimes(lat: Double, lon: Double): Response<PrayerResponse> {
        return apiService.getPrayerTimes(lat, lon)
    }
}