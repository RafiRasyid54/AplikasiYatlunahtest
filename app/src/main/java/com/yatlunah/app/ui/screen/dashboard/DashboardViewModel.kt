package com.yatlunah.app.ui.screen.dashboard

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.repository.AuthRepository
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val repository = AuthRepository()

    var streak by mutableStateOf("0 Hari")
    var lastRead by mutableStateOf("Jilid 1")
    var lastPage by mutableStateOf("Hal. 0")

    // ✅ Perbaikan: Gunakan mutableFloatStateOf agar lebih optimal
    var progressPercent by mutableFloatStateOf(0f)

    var isLoading by mutableStateOf(false)

    fun fetchStats(userId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getUserStats(userId)
                // Di dalam DashboardViewModel.kt
                if (response.isSuccessful) {
                    val data = response.body()
                    streak = "${data?.currentStreak ?: 0} Hari"
                    lastRead = "Jilid ${data?.lastJilid ?: 1}"
                    lastPage = "Halaman ${data?.lastHalaman ?: 0}"

                    // ✅ Sekarang sudah sinkron dengan model
                    progressPercent = data?.totalProgress ?: 0f
                }
            } catch (e: Exception) {
                Log.e("DashboardVM", "Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}