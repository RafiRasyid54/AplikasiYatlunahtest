package com.yatlunah.app.ui.screen.admin

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.model.UserResponse
import com.yatlunah.app.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val repository = AuthRepository()

    var userList by mutableStateOf<List<UserResponse>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun fetchUsersByRole(role: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Pastikan fungsi ini sudah kamu tambahkan di AuthRepository & ApiService
                val response = repository.getUsersByRole(role)
                if (response.isSuccessful) {
                    userList = response.body() ?: emptyList()
                } else {
                    errorMessage = "Gagal mengambil data: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error Koneksi: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}