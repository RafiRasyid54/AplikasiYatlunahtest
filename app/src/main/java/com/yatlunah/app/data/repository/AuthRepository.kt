package com.yatlunah.app.data.repository

import com.yatlunah.app.data.model.*
import com.yatlunah.app.data.remote.RetrofitClient
import com.yatlunah.app.data.remote.dto.UpdateNameRequest    // ✅ Import yang benar
import com.yatlunah.app.data.remote.dto.UpdatePasswordRequest // ✅ Import yang benar
import com.yatlunah.app.data.model.QuotesHarian
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthRepository {
    private val apiService = RetrofitClient.instance

    // --- Autentikasi ---
    suspend fun register(request: RegisterRequest) = apiService.register(request)
    suspend fun login(request: LoginRequest) = apiService.login(request)

    // --- Statistik ---
    // Contoh jika ada pemrosesan data di Repository
    suspend fun getUserStats(userId: String): Response<UserStats> = withContext(Dispatchers.IO) {
        apiService.getUserStats(userId)
    }


    suspend fun getUsersByRole(role: String, idMitra: String? = null): Response<List<UserResponse>> {
        return apiService.getUsersByRole(role, idMitra)
    }
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

    suspend fun addQuote(teks: String, sumber: String): Response<AuthResponse> {
        // SALAH (seperti kodinganmu sekarang):
        // return apiService.addQuote(teks, sumber)

        // BENAR: Bungkus teks dan sumber ke dalam objek QuotesHarian dulu
        val quoteObject = QuotesHarian(
            teksQuote = teks,
            sumber = sumber
        )

        // Kirim objek tersebut ke apiService
        return apiService.addQuote(quoteObject)
    }
    // Di dalam class AuthRepository
    suspend fun getRandomQuote(): Response<QuotesHarian> {
        return apiService.getRandomQuote()
    }

    // Di AuthRepository.kt
    suspend fun updateUserRole(userId: String, role: String): Response<Void> {
        return apiService.updateUserRole(userId, role) // Nama fungsi apiService harus sama dengan di ApiService.kt
    }

    // Di dalam class AuthRepository
    suspend fun getAllMitra(): Response<List<Mitra>> {
        return RetrofitClient.authApi.getAllMitra()
    }
}