package com.yatlunah.app.data.repository

import android.util.Log
import com.yatlunah.app.data.remote.RetrofitClient
import com.yatlunah.app.data.remote.dto.UpdateNameRequest
import com.yatlunah.app.data.remote.dto.UpdatePasswordRequest

class UserRepository {
    // Memanggil Retrofit yang sudah kamu buat
    private val api = RetrofitClient.instance

    suspend fun updateUserName(userId: String, newName: String): Boolean {
        return try {
            val request = UpdateNameRequest(newName)
            val response = api.updateName(userId, request)

            if (response.isSuccessful) {
                true
            } else {
                Log.e("UserRepository", "Gagal update nama: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error jaringan: ${e.message}")
            false
        }
    }

    // ✅ Tambahkan parameter oldPassword agar lebih aman
    suspend fun updateUserPassword(userId: String, oldPassword: String, newPassword: String): Boolean {
        return try {
            val request = UpdatePasswordRequest(oldPassword, newPassword)
            val response = api.updatePassword(userId, request)

            if (response.isSuccessful) {
                true
            } else {
                Log.e("UserRepository", "Gagal ganti password: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error jaringan: ${e.message}")
            false
        }
    }
}