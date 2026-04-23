package com.yatlunah.app.data.remote

import com.yatlunah.app.data.model.PrayerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PrayerApiService {
    @GET("v1/timings")
    suspend fun getPrayerTimings(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("method") method: Int = 11
    ): Response<PrayerResponse>
}