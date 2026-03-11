package com.yatlunah.app.data.repository

import android.util.Log
import com.yatlunah.app.data.model.JilidData
import com.yatlunah.app.data.remote.ProgressRequest
import com.yatlunah.app.data.remote.RetrofitClient

class MateriRepository {

    // Mengambil API dari RetrofitClient
    private val apiService = RetrofitClient.materiApi

    // Mengambil daftar jilid
    suspend fun getDaftarJilid(): List<JilidData> {
        return try {
            apiService.getDaftarJilid()
        } catch (e: Exception) {
            Log.e("MateriRepository", "Error getDaftarJilid: ${e.message}")
            emptyList()
        }
    }

    // Menyimpan progress santri
    suspend fun saveProgress(userId: String, jilidId: Int, halaman: Int) {
        try {
            val request = ProgressRequest(userId, jilidId, halaman)
            val response = apiService.updateProgressToDatabase(request)

            // ✅ Menggunakan response.status dari ProgressResponse
            Log.d("MateriRepository", "Progress Updated: ${response.status}")
        } catch (e: Exception) {
            Log.e("MateriRepository", "Error saveProgress: ${e.message}")
        }
    }

    // Mengambil URL Audio untuk materi ustadz
    suspend fun getAudioUrl(jilidId: Int, halaman: Int): String? {
        return try {
            val response = apiService.getAudioUrl(jilidId, halaman)
            response.audioUrl
        } catch (e: Exception) {
            Log.e("MateriRepository", "Error getAudioUrl: ${e.message}")
            null
        }
    }
}