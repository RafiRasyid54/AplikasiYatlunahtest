package com.yatlunah.app.data.repository

import com.yatlunah.app.data.model.ProgramYatlunah
import com.yatlunah.app.data.remote.BimbinganApiService

class ProgramRepository(private val apiService: BimbinganApiService) {

    suspend fun getAllPrograms(): Result<List<ProgramYatlunah>> {
        return try {
            // Memanggil fungsi yang sudah kita tambahkan di ApiService
            val response = apiService.getPrograms()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}