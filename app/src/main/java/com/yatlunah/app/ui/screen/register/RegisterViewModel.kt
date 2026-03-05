package com.yatlunah.app.ui.screen.register

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.model.RegisterRequest
import com.yatlunah.app.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val repository = AuthRepository()

    var registerStatus by mutableStateOf("")
    var isLoading by mutableStateOf(false) // Tambahkan loading state agar UI lebih interaktif

    fun register(nama: String, email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.register(RegisterRequest(nama, email, pass))
                if (response.isSuccessful) {
                    // ✅ PERBAIKAN: Ganti user_id menjadi userId
                    val idBaru = response.body()?.userId
                    registerStatus = "Registrasi Berhasil!"
                    onSuccess() // Jalankan navigasi ke Login jika sukses
                } else {
                    // Ambil pesan error dari backend FastAPI (misal: "Email sudah terdaftar")
                    val errorMsg = response.errorBody()?.string() ?: "Gagal mendaftar"
                    registerStatus = errorMsg
                }
            } catch (e: Exception) {
                registerStatus = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}