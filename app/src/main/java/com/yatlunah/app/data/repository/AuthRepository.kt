package com.yatlunah.app.data.repository

import com.yatlunah.app.data.model.*
import com.yatlunah.app.data.remote.RetrofitClient
import com.yatlunah.app.data.remote.dto.UpdateNameRequest    // ✅ Import yang benar
import com.yatlunah.app.data.remote.dto.UpdatePasswordRequest // ✅ Import yang benar
import retrofit2.Response

class AuthRepository {
    private val apiService = RetrofitClient.instance

    // --- Autentikasi ---
    suspend fun register(request: RegisterRequest) = apiService.register(request)
    suspend fun login(request: LoginRequest) = apiService.login(request)

    // --- Statistik ---
    suspend fun getUserStats(userId: String) = apiService.getUserStats(userId)

    suspend fun updateName(userId: String, request: UserNameUpdate) =
        apiService.updateName(userId, request)

    suspend fun updatePassword(userId: String, request: UserPasswordUpdate) =
        apiService.updatePassword(userId, request)

    // --- Progress ---
    suspend fun updateProgress(userId: String, jilid: Int, halaman: Int) =
        apiService.updateProgress(userId, jilid, halaman)

    // = = = list User Berdasarkan role - - -
    // Tambahkan di dalam class AuthRepository
    suspend fun getUsersByRole(role: String): Response<List<UserResponse>> {
        return apiService.getUsersByRole(role)
    }
}