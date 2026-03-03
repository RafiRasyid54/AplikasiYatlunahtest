package com.yatlunah.app.data.repository

import com.yatlunah.app.data.model.*
import com.yatlunah.app.data.remote.RetrofitClient
import retrofit2.Response // ✅ Pastikan Response di-import dengan benar

class AuthRepository {
    // Gunakan apiService yang sudah didefinisikan agar kode konsisten
    private val apiService = RetrofitClient.instance

    suspend fun registerUser(request: RegisterRequest) = apiService.register(request)

    suspend fun loginUser(request: LoginRequest) = apiService.login(request)

    // ✅ Sederhanakan pemanggilan agar tidak redundan
    suspend fun getUserStats(userId: String): Response<UserStats> {
        return apiService.getUserStats(userId)
    }
    suspend fun updateProgress(userId: String, jilid: Int, halaman: Int) = apiService.updateProgress(userId, jilid, halaman)

    // Di dalam class AuthRepository
    suspend fun updateProfileName(userId: String, request: UserNameUpdate) = apiService.updateProfileName(userId, request)

    // Pastikan updatePassword juga sudah benar seperti ini
    suspend fun updatePassword(userId: String, request: UserPasswordUpdate) = apiService.updatePassword(userId, request)
}