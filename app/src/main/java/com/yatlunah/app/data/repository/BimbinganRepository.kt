package com.yatlunah.app.data.repository

import com.yatlunah.app.data.model.BimbinganRequest
import com.yatlunah.app.data.remote.BimbinganApiService
import java.time.LocalDate

class BimbinganRepository(private val apiService: BimbinganApiService) {
    suspend fun daftarBimbingan(userId: String, jenis: String): Result<Unit> {
        return try {
            val request = BimbinganRequest(
                userId = userId,
                jenisBimbingan = jenis,
                tanggalDaftar = LocalDate.now().toString()
            )
            val response = apiService.postBimbingan(request)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Gagal mendaftar"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}