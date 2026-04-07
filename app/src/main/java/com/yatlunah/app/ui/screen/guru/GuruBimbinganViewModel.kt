package com.yatlunah.app.ui.screen.guru

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.model.Bimbingan
import com.yatlunah.app.data.model.UpdateStatusRequest
import com.yatlunah.app.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GuruBimbinganViewModel : ViewModel() {

    private val _antreanList = MutableStateFlow<List<Bimbingan>>(emptyList())
    val antreanList: StateFlow<List<Bimbingan>> = _antreanList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchAntrean() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // ✅ Pastikan .getBimbinganMenunggu() ada di ApiService
                val response = RetrofitClient.bimbinganApi.getBimbinganMenunggu()
                if (response.isSuccessful) {
                    _antreanList.value = response.body()?.data ?: emptyList()
                } else {
                    _errorMessage.value = "Gagal memuat: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateStatus(bimbinganId: Int, status: String, idGuru: String) {
        viewModelScope.launch {
            try {
                val request = UpdateStatusRequest(
                    status = status,
                    idGuru = idGuru // Dikirim ke kolom id_guru di DB
                )

                // ✅ Pastikan .updateStatusBimbingan() ada di ApiService
                val response = RetrofitClient.bimbinganApi.updateStatusBimbingan(bimbinganId, request)

                if (response.isSuccessful) {
                    fetchAntrean() // Refresh list otomatis
                } else {
                    _errorMessage.value = "Gagal Update: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }
}