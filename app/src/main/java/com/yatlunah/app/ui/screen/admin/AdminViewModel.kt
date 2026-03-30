package com.yatlunah.app.ui.screen.admin

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.model.UserResponse
import com.yatlunah.app.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val repository = AuthRepository()

    // --- State untuk Manajemen User ---
    var userList by mutableStateOf<List<UserResponse>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // --- State untuk Manajemen Quote ---
    var isQuoteSavedSuccess by mutableStateOf(false)

    fun fetchUsersByRole(role: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
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

    // ✅ TAMBAHKAN FUNGSI INI
    fun saveQuote(teks: String, sumber: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Pastikan addQuote sudah ada di AuthRepository
                val response = repository.addQuote(teks, sumber)
                if (response.isSuccessful) {
                    isQuoteSavedSuccess = true
                } else {
                    errorMessage = "Gagal simpan quote"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun resetQuoteStatus() {
        isQuoteSavedSuccess = false
    }

    fun clearErrorMessage() {
        errorMessage = null
    }
}