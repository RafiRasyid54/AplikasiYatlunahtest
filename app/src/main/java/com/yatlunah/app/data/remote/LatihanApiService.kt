package com.yatlunah.app.data.remote

import com.yatlunah.app.data.model.LatihanSoal
import com.yatlunah.app.data.model.ProgresLatihanRequest
import retrofit2.Response
import retrofit2.http.*

interface LatihanApiService {
    // Digunakan Admin/Guru untuk input soal dari dokumen bank soal
    @POST("latihan-soal")
    suspend fun tambahSoalLatihan(@Body soal: LatihanSoal): Response<Unit>

    // Fungsi yang menyebabkan error jika belum ada
// data/remote/LatihanApiService.kt
    @GET("latihan-soal/check")
    suspend fun getSoalByMapping(
        @Query("jilid") jilid: Int,
        @Query("halaman") halaman: Int
    ): Response<List<LatihanSoal>> // Mengembalikan List agar bisa menampung banyak soal di satu halaman

    @POST("latihan-soal/progres")
    suspend fun simpanProgresLatihan(@Body request: ProgresLatihanRequest): Response<Unit>

    @GET("latihan-soal/all")
    suspend fun getAllSoal(): Response<List<LatihanSoal>>

    @DELETE("latihan-soal/{id}")
    suspend fun deleteSoal(@Path("id") id: Int): Response<Unit>

}