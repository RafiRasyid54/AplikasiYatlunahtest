package com.yatlunah.app.data.repository

import com.yatlunah.app.data.model.PrayerResponse
import com.yatlunah.app.data.remote.PrayerApiService
import retrofit2.Response

class PrayerRepository(private val apiService: PrayerApiService) {
    // Pastikan nama fungsi ini sama dengan yang dipanggil di ViewModel
    suspend fun getPrayerTimes(lat: Double, lon: Double): Response<PrayerResponse> {
        return apiService.getPrayerTimings(lat, lon)
    }
}