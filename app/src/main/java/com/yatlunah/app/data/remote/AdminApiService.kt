package com.yatlunah.app.data.remote

import com.yatlunah.app.data.model.AdminStatsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

// Model untuk mendapatkan Nama Mitra (Sesuai yang kita buat sebelumnya)
data class MitraInfoResponse(
    val id: String,
    val nama_lembaga: String,
    val kota: String?
)

// ✅ TAMBAHKAN MODEL INI: Untuk menangkap angka statistik Mitra
data class MitraStatsResponse(
    val total_guru: Int,
    val total_santri: Int,
    val total_user: Int
)

interface AdminApiService {
    @GET("admin/statistik")
    suspend fun getDashboardStats(): Response<AdminStatsResponse>

    @GET("mitra/{id_mitra}")
    suspend fun getMitraInfo(@Path("id_mitra") idMitra: String): Response<MitraInfoResponse>

    // ✅ TAMBAHKAN ENDPOINT INI
    @GET("mitra/{id_mitra}/statistik")
    suspend fun getMitraStatistik(@Path("id_mitra") idMitra: String): Response<MitraStatsResponse>
}