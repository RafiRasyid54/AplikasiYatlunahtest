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

    // ✅ SESUAIKAN: Parameter onSuccess sekarang menerima TIGA String (ID, Nama, dan Email)
    fun login(email: String, pass: String, onSuccess: (String, String, String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.loginUser(LoginRequest(email, pass))

                Log.d("YATLUNAH_DEBUG", "Response: ${response.body()}")

                if (response.isSuccessful) {
                    val body = response.body()

                    // ✅ Ambil data lengkap dari backend FastAPI kamu
                    val userId = body?.userId ?: "" // Ambil UUID asli
                    val name = body?.nama_lengkap ?: "User Yatlunah"
                    val userEmail = body?.email ?: email // Gunakan email dari input jika body null

                    loginStatus = "Selamat datang, $name!"

                    // ✅ KIRIM KETIGA DATA ke LoginScreen -> MainActivity
                    onSuccess(userId, name, userEmail)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Kesalahan tidak diketahui"
                    loginStatus = "Gagal: Email atau Password salah"
                    Log.e("YATLUNAH_DEBUG", "Error Login: $errorMsg")
                }
            } catch (e: Exception) {
                loginStatus = "Koneksi Gagal: ${e.message}"
                Log.e("YATLUNAH_DEBUG", "Exception: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}