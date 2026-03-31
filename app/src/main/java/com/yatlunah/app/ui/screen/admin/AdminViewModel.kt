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

    fun saveQuote(teks: String, sumber: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                // SOLUSI: Buat objek QuotesHarian dulu
                val newQuote = QuotesHarian(
                    teksQuote = teks, // Pastikan nama properti sesuai data class kamu
                    sumber = sumber
                )

                // Sekarang kirim SATU objek (newQuote), bukan dua String
                val response = apiService.addQuote(newQuote)

                if (response.isSuccessful) {
                    isQuoteSavedSuccess = true
                    getAllQuotes()
                } else {
                    errorMessage = "Gagal simpan: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    // CATATAN: Fungsi updateQuote dan deleteQuote akan ERROR
    fun updateQuote(id: Int, teks: String, sumber: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val updatedData = QuotesHarian(id = id, teksQuote = teks, sumber = sumber)
                // Gunakan 'id' untuk Path, dan 'updatedData' untuk Body
                val response = apiService.updateQuote(id, updatedData)

                if (response.isSuccessful) {
                    isQuoteSavedSuccess = true
                    getAllQuotes()
                }
            } catch (e: Exception) {
                errorMessage = e.message
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