package com.yatlunah.app.ui.screen.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.model.QuotesHarian
import com.yatlunah.app.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    // Gunakan instance yang bertipe AuthApiService
    private val apiService = RetrofitClient.instance

    private val _quotes = MutableStateFlow<List<QuotesHarian>>(emptyList())
    val quotes: StateFlow<List<QuotesHarian>> = _quotes.asStateFlow()

    var isLoading by mutableStateOf(false)
    var isQuoteSavedSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        getAllQuotes()
    }

    fun fetchQuotes() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.materiApi.getAllQuotes() // Pastikan endpoint ini ada di ApiService
                if (response.isSuccessful) {
                    _quotes.value = response.body() ?: emptyList()
                } else {
                    errorMessage = "Gagal mengambil data: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Koneksi Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun getAllQuotes() {
        viewModelScope.launch {
            isLoading = true
            try {
                // Jika kamu sudah tambah getAllQuotes() di interface:
                val response = apiService.getAllQuotes()

                // ATAU jika tetap pakai yang lama:
                // val response = apiService.getRandomQuote()

                if (response.isSuccessful) {
                    _quotes.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    // app/src/main/java/com/yatlunah/app/ui/screen/admin/AdminViewModel.kt

    fun saveQuote(teks: String, sumber: String, hari: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val newQuote = QuotesHarian(teksQuote = teks, sumber = sumber, hari = hari)
                val response = RetrofitClient.materiApi.tambahQuote(newQuote)
                if (response.isSuccessful) {
                    isQuoteSavedSuccess = true
                    fetchQuotes() // Refresh daftar
                } else {
                    errorMessage = "Gagal menyimpan: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    // FIX: Tambahkan fungsi updateQuote yang hilang
    fun updateQuote(id: Int, teks: String, sumber: String, hari: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val updatedQuote = QuotesHarian(id = id, teksQuote = teks, sumber = sumber, hari = hari)
                val response = RetrofitClient.materiApi.updateQuote(id, updatedQuote)
                if (response.isSuccessful) {
                    isQuoteSavedSuccess = true
                    fetchQuotes() // Refresh daftar
                } else {
                    errorMessage = "Gagal memperbarui: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteQuote(id: Int) { // Parameter 'id' sekarang digunakan di bawah
        viewModelScope.launch {
            try {
                val response = apiService.deleteQuote(id)
                if (response.isSuccessful) {
                    getAllQuotes()
                }
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun resetQuoteStatus() {
        isQuoteSavedSuccess = false
        errorMessage = null
    }
}