package com.yatlunah.app.data.repository

import android.util.Log
import com.yatlunah.app.data.remote.RetrofitClient
// ✅ PERBAIKAN: Gunakan model UserNameUpdate & UserPasswordUpdate (Bukan Update...Request)
import com.yatlunah.app.data.model.UserNameUpdate
import com.yatlunah.app.data.model.UserPasswordUpdate

class UserRepository {
    private val api = RetrofitClient.instance

    suspend fun updateUserName(userId: String, newName: String): Boolean {
        return try {
            // ✅ Gunakan UserNameUpdate sesuai kontrak di AuthApiService
            val request = UserNameUpdate(nama_lengkap = newName)
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

    suspend fun updateUserPassword(userId: String, oldPassword: String, newPassword: String): Boolean {
        return try {
            // ✅ Gunakan UserPasswordUpdate sesuai kontrak di AuthApiService
            val request = UserPasswordUpdate(
                old_password = oldPassword,
                new_password = newPassword
            )
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