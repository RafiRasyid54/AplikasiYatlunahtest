package com.yatlunah.app.data.repository

import android.util.Log
import com.yatlunah.app.data.remote.RetrofitClient
import com.yatlunah.app.data.model.UserNameUpdate
import com.yatlunah.app.data.model.UserPasswordUpdate
import com.yatlunah.app.data.model.UserResponse // Pastikan import ini ada
import retrofit2.Response

class UserRepository {
    private val api = RetrofitClient.instance

    /**
     * Mengambil daftar user berdasarkan role dan filter id_mitra.
     * Fungsi ini menghubungkan ViewModel ke AuthApiService.
     */
    suspend fun getUsersByMitra(role: String, idMitra: String): Response<List<UserResponse>> {
        return api.getUsersByRole(role, idMitra) // Memanggil endpoint admin/users/{role}
    }

    suspend fun getMitraGroups(idMitra: String) =
        api.getMitraGroups(idMitra)

    suspend fun getGroupDetails(guruId: String) =
        api.getGroupDetails(guruId)


    suspend fun updateUserName(userId: String, newName: String): Boolean {
        return try {
            val request = UserNameUpdate(nama_lengkap = newName)
            val response = api.updateName(userId, request) //

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
            val request = UserPasswordUpdate(
                old_password = oldPassword,
                new_password = newPassword
            )
            val response = api.updatePassword(userId, request) //

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