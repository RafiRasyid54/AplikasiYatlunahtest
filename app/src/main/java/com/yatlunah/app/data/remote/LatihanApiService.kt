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

    @GET("latihan-soal/all") // Pastikan tidak ada typo
    suspend fun getAllSoal(): Response<List<LatihanSoal>>

    @POST("latihan-soal")
    suspend fun tambahSoalLatihan(@Body soal: LatihanSoal): Response<Unit>

    @PUT("latihan-soal/{id}")
    suspend fun updateSoal(
        @Path("id") id: Int,
        @Body soal: LatihanSoal
    ): Response<Unit>

    @DELETE("latihan-soal/{id}")
    suspend fun deleteSoal(
        @Path("id") id: Int
    ): Response<Unit>

    @GET("latihan-soal/check")
    suspend fun getSoalByMapping(
        @Query("jilid") jilid: Int,
        @Query("halaman") halaman: Int
    ): Response<List<LatihanSoal>>
}