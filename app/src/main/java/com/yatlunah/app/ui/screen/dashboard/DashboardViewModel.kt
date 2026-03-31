package com.yatlunah.app.ui.screen.dashboard

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.repository.AuthRepository
import com.yatlunah.app.data.model.QuotesHarian
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardViewModel : ViewModel() {
    private val repository = AuthRepository()

    var streak by mutableStateOf("0 Hari")
    var lastRead by mutableStateOf("Jilid 1")
    var lastPage by mutableStateOf("Hal. 0")
    var progressPercent by mutableFloatStateOf(0f)
    var isLoading by mutableStateOf(false)
    var currentQuote by mutableStateOf("Memuat inspirasi...")
    var currentSource by mutableStateOf("")

    fun fetchStats(userId: String) {
        // Tambahkan Dispatchers.IO di sini agar tidak mengunci layar
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            try {
                val response = repository.getUserStats(userId)
                // Update state UI harus kembali ke Dispatchers.Main
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        streak = "${data?.currentStreak ?: 0} Hari"
                        lastRead = "Jilid ${data?.lastJilid ?: 1}"
                        lastPage = "Halaman ${data?.lastHalaman ?: 0}"
                        progressPercent = data?.totalProgress ?: 0f
                    }
                }
            } catch (e: Exception) {
                Log.e("DashboardVM", "Error: ${e.message}")
            } finally {
                // Pastikan isLoading berhenti di thread utama
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    fun startQuoteTimer() {
        viewModelScope.launch(Dispatchers.IO) { // Jalankan di IO
            while (true) {
                try {
                    val response = repository.getRandomQuote()
                    withContext(Dispatchers.Main) { // Update ke UI thread
                        if (response.isSuccessful) {
                            val data = response.body()
                            currentQuote = data?.teksQuote ?: "Tidak ada kutipan"
                            currentSource = data?.sumber ?: ""
                        }
                    }
                } catch (e: Exception) {
                    Log.e("DashboardVM", "Error fetchQuote: ${e.message}")
                }
                delay(60000L) // Delay 5 menit
            }
        }
    }
}