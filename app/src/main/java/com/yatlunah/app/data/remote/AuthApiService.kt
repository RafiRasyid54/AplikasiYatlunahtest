package com.yatlunah.app.data.remote

import com.yatlunah.app.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    // --- 1. Statistik & Info User ---
    @GET("users/{id}/stats")
    suspend fun getUserStats(@Path("id") userId: String): Response<UserStats>

    // --- 2. Autentikasi (Login & Register) ---
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // --- 3. Update Profile ---
    @PUT("users/{id}/name")
    suspend fun updateName(
        @Path("id") userId: String,
        @Body request: UserNameUpdate
    ): Response<AuthResponse>

    @PUT("users/{id}/password")
    suspend fun updatePassword(
        @Path("id") userId: String,
        @Body request: UserPasswordUpdate
    ): Response<AuthResponse>

    // --- 4. Penilaian Guru & Update Progress Otomatis ---
    @POST("setoran/nilai")
    suspend fun beriNilaiSetoran(
        @Body request: SetoranPenilaianRequest
    ): Response<AuthResponse>

    // --- 5. Progres Belajar manual ---
    @POST("users/{user_id}/update_progress")
    suspend fun updateProgress(
        @Path("user_id") userId: String,
        @Query("jilid") jilid: Int,
        @Query("halaman") halaman: Int
    ): Response<ResponseBody>

    // --- 6. Admin Panel ---
    @GET("admin/users/{role}")
    suspend fun getUsersByRole(
        @Path("role") role: String,
        @Query("id_mitra") idMitra: String? = null
    ): Response<List<UserResponse>>

    @GET("admin/users/count")
    suspend fun getUsersCount(): Response<Map<String, Int>>

    @PUT("admin/users/{user_id}/role")
    suspend fun updateRole(
        @Path("user_id") userId: String,
        @Query("new_role") role: String
    ): Response<AuthResponse>

    // PERBAIKAN: Gunakan @Query agar sesuai dengan FastAPI

    @FormUrlEncoded
    @POST("user/update-role") // Sesuaikan dengan endpoint backend Anda
    suspend fun updateUserRole(
        @Field("id") userId: String,
        @Field("role") role: String
    ): Response<Void>

    // Tambahkan ini di dalam AuthApiService.kt

    // Untuk mengambil semua daftar quote

    // Di AuthApiService.kt
    @GET("quotes/random") // Pastikan tidak ada typo seperti "quotes" atau "/quote/random/"
    suspend fun getRandomQuote(): Response<QuotesHarian>

    @GET("admin/quotes")
    suspend fun getAllQuotes(): Response<List<QuotesHarian>>
    // Pakai @Body untuk mengirim objek JSON
    @POST("admin/quotes")
    suspend fun addQuote(@Body quote: QuotesHarian): Response<AuthResponse>

    // Pakai @Path untuk ID di URL, dan @Body untuk data update
    @PUT("admin/quotes/{id}")
    suspend fun updateQuote(
        @Path("id") id: Int,
        @Body quote: QuotesHarian
    ): Response<MessageResponse> // Ganti ke MessageResponse

    @DELETE("admin/quotes/{id}")
    suspend fun deleteQuote(
        @Path("id") id: Int
    ): Response<MessageResponse> // Ganti ke MessageResponse
}