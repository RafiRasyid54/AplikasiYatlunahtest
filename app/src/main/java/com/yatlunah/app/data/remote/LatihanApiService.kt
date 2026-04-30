package com.yatlunah.app.data.remote

import com.yatlunah.app.data.model.LatihanSoal
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface LatihanApiService {

    // 1. Ambil semua soal (Tadinya error: Unresolved reference 'getAllSoal')
    @GET("latihan-soal")
    suspend fun getAllSoal(): Response<List<LatihanSoal>>

    // 2. Tambah soal baru
    @POST("latihan-soal")
    suspend fun tambahSoalLatihan(@Body soal: LatihanSoal): Response<Unit>

    // 3. Update soal (Tadinya error: Unresolved reference 'updateSoal')
    @PUT("latihan-soal/{id}")
    suspend fun updateSoal(
        @Path("id") id: Int,
        @Body soal: LatihanSoal
    ): Response<Unit>

    // 4. Hapus soal (Tadinya error: Unresolved reference 'deleteSoal')
    @DELETE("latihan-soal/{id}")
    suspend fun deleteSoal(
        @Path("id") id: Int
    ): Response<Unit>

    // Fungsi tambahan yang Anda buat sebelumnya
    @GET("latihan-soal/check")
    suspend fun getSoalByMapping(
        @Query("jilid") jilid: Int,
        @Query("halaman") halaman: Int
    ): Response<List<LatihanSoal>>
}