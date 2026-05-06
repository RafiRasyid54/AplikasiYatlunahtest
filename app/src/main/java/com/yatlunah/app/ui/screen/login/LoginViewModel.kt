package com.yatlunah.app.ui.screen.login

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.model.LoginRequest
import com.yatlunah.app.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    var loginStatus by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun login(email: String, pass: String, onSuccess: (String, String, String, String, String?) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.login(LoginRequest(email, pass))
                if (response.isSuccessful) {
                    val body = response.body()

                    val userId = body?.userId ?: ""
                    val name = body?.nama_lengkap ?: "User"
                    val userEmail = body?.email ?: email
                    val userRole = body?.role ?: "santri"
                    val idMitra = body?.idMitra // ✅ Ambil idMitra dari AuthResponse

                    loginStatus = "Selamat datang, $name!"

                    // ✅ Tambahkan parameter kelima (idMitra) ke callback
                    onSuccess(userId, name, userEmail, userRole, idMitra)
                } else {
                    loginStatus = "Gagal: Email atau Password salah"
                }
            } catch (e: Exception) {
                loginStatus = "Koneksi Gagal: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}