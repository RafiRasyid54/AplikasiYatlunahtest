package com.yatlunah.app.data.remote

import com.yatlunah.app.data.model.*
import com.yatlunah.app.data.remote.dto.UpdateNameRequest
import com.yatlunah.app.data.remote.dto.UpdatePasswordRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApiService {

    // Di AuthApiService.kt
    @GET("users/{id}/stats")
    suspend fun getUserStats(@Path("id") userId: String): Response<UserStats>
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // ✅ ENDPOINT BARU: Update Nama
    // Pastikan "users/{id}/name" sesuai dengan rute di FastAPI kamu (@app.put("/users/{id}/name"))
    @PUT("users/{id}/name")
    suspend fun updateName(
        @Path("id") userId: String,
        @Body request: UpdateNameRequest
    ): Response<AuthResponse> // Menggunakan AuthResponse atau sesuaikan dengan kembalian FastAPI

    @PUT("users/{id}/password")
    suspend fun updatePassword(
        @Path("id") userId: String,
        @Body request: UpdatePasswordRequest
    ): Response<AuthResponse> // Menggunakan AuthResponse atau sesuaikan dengan kembalian FastAPI

    @POST("users/{user_id}/update_progress")
    suspend fun updateProgress(
        @Path("user_id") userId: String,
        @Query("jilid") jilid: Int,
        @Query("halaman") halaman: Int
    ): Response<ResponseBody>

    // Update Profile
    @PUT("users/{id}/update-profile")
    suspend fun updateProfileName(
        @Path("id") userId: String,
        @Body request: UserNameUpdate
    ): Response<okhttp3.ResponseBody>

    @PUT("users/{id}/update-password")
    suspend fun updatePassword(
        @Path("id") userId: String,
        @Body request: UserPasswordUpdate
    ): Response<okhttp3.ResponseBody>

}