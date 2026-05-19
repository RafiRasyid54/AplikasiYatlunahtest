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
            loginStatus = "" // Bersihkan status lama setiap kali tombol diklik

            // Variabel penampung data agar bisa dieksekusi di luar blok try-catch
            var navigateData: NavigateData? = null

            try {
                val response = repository.login(LoginRequest(email, pass))
                if (response.isSuccessful) {
                    val body = response.body()

                    val userId = body?.userId ?: ""
                    val name = body?.nama_lengkap ?: "User"
                    val userEmail = body?.email ?: email
                    val userRole = body?.role ?: "santri"
                    val idMitra = body?.idMitra

                    loginStatus = "Selamat datang, $name!"

                    // Simpan data ke penampung
                    navigateData = NavigateData(userId, name, userEmail, userRole, idMitra)
                } else {
                    loginStatus = "Gagal: Email atau Password salah (Code: ${response.code()})"
                }
            } catch (e: Exception) {
                loginStatus = "Koneksi Gagal: ${e.localizedMessage}"
                Log.e("YATLUNAH_DEBUG", "Error login ke Railway: ${e.message}")
            } finally {
                // Matikan loading TERLEBIH DAHULU agar UI siap berpindah halaman
                isLoading = false
            }

            // Jalankan navigasi di sini (UI thread sudah bebas dari beban loading)
            navigateData?.let { data ->
                onSuccess(data.userId, data.name, data.userEmail, data.userRole, data.idMitra)
            }
        }
    }

    // Helper data class lokal untuk merapikan passing data navigasi
    private data class NavigateData(
        val userId: String,
        val name: String,
        val userEmail: String,
        val userRole: String,
        val idMitra: String?
    )
}