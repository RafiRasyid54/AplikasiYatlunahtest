package com.yatlunah.app.data.remote

import com.yatlunah.app.data.model.*
import com.yatlunah.app.data.remote.dto.UpdateNameRequest
import com.yatlunah.app.data.remote.dto.UpdatePasswordRequest
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

    // --- 3. Update Profile (Nama) ---
    // Pilih satu endpoint yang paling sesuai dengan backend FastAPI kamu
    @PUT("users/{id}/name")
    suspend fun updateName(
        @Path("id") userId: String,
        @Body request: UserNameUpdate // 👈 Harus pakai ini
    ): Response<AuthResponse>

    @PUT("users/{id}/password")
    suspend fun updatePassword(
        @Path("id") userId: String,
        @Body request: UserPasswordUpdate // 👈 Harus pakai ini
    ): Response<AuthResponse>

    // --- 5. Progres Belajar ---
    @POST("users/{user_id}/update_progress")
    suspend fun updateProgress(
        @Path("user_id") userId: String,
        @Query("jilid") jilid: Int,
        @Query("halaman") halaman: Int
    ): Response<ResponseBody>

    // mengambil data user untuk admin
    @GET("admin/users/{role}")
    suspend fun getUsersByRole(
        @Path("role") role: String
    ): Response<List<UserResponse>> // Pastikan UserResponse sudah ada di folder model

}