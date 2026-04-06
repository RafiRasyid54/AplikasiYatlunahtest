package com.yatlunah.app.ui.screen.bimbingan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.remote.BimbinganApiService
import com.yatlunah.app.data.remote.RetrofitClient
import com.yatlunah.app.data.repository.BimbinganRepository
import kotlinx.coroutines.launch

class BimbinganViewModel(private val repository: BimbinganRepository) : ViewModel() {

    // State UI untuk memantau proses pendaftaran bimbingan [cite: 161]
    var uiState by mutableStateOf<BimbinganUiState>(BimbinganUiState.Idle)
        private set // Bagus untuk enkapsulasi agar tidak diubah dari luar

    // Di dalam BimbinganViewModel.kt
    fun submitPendaftaran(userId: String, jenis: String) {
        viewModelScope.launch {
            uiState = BimbinganUiState.Loading
            val result = repository.daftarBimbingan(userId, jenis)

            uiState = if (result.isSuccess) {
                BimbinganUiState.Success
            } else {
                // Tampilkan pesan error asli untuk debug
                val errorMsg = result.exceptionOrNull()?.message ?: "Terjadi kesalahan"
                BimbinganUiState.Error(errorMsg)
            }
        }
    }

    // Factory diperlukan karena BimbinganViewModel memiliki parameter constructor [cite: 149]
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // PERBAIKAN: Tambahkan '.retrofit' sebelum '.create'
                val apiService = RetrofitClient.retrofit.create(BimbinganApiService::class.java)
                val repository = BimbinganRepository(apiService)
                return BimbinganViewModel(repository) as T
            }
        }
    }
}

// State untuk menangani feedback visual kepada pengguna [cite: 171]
sealed class BimbinganUiState {
    object Idle : BimbinganUiState()
    object Loading : BimbinganUiState()
    object Success : BimbinganUiState()
    data class Error(val message: String) : BimbinganUiState()
}