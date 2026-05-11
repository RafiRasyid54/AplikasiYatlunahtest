package com.yatlunah.app.ui.screen.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.model.QuotesHarian
import com.yatlunah.app.data.model.LatihanSoal
import com.yatlunah.app.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    // --- STATE MANAGEMENT ---

    // State untuk Quote
    private val _quotes = MutableStateFlow<List<QuotesHarian>>(emptyList())
    val quotes: StateFlow<List<QuotesHarian>> = _quotes.asStateFlow()

    // State untuk Soal Latihan
    private val _questions = MutableStateFlow<List<LatihanSoal>>(emptyList())
    val questions: StateFlow<List<LatihanSoal>> = _questions.asStateFlow()

    // State UI Umum
    var isLoading by mutableStateOf(false)
    var isActionSuccess by mutableStateOf(false) // Digunakan bersama untuk Soal & Quote
    var isQuoteSavedSuccess by mutableStateOf(false) // Deprecated: Digunakan oleh screen Quote lama
    var errorMessage by mutableStateOf<String?>(null)

    init {
        fetchQuotes()
        fetchQuestions()
    }

    // --- LOGIC UNTUK QUOTE ---

    fun fetchQuotes() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.materiApi.getAllQuotes()
                if (response.isSuccessful) {
                    _quotes.value = response.body() ?: emptyList()
                } else {
                    errorMessage = "Gagal mengambil quote: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Koneksi Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun saveQuote(teks: String, sumber: String, hari: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val newQuote = QuotesHarian(teksQuote = teks, sumber = sumber, hari = hari)
                val response = RetrofitClient.materiApi.tambahQuote(newQuote)
                if (response.isSuccessful) {
                    isActionSuccess = true
                    isQuoteSavedSuccess = true
                    fetchQuotes()
                } else {
                    errorMessage = "Gagal menyimpan quote: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun updateQuote(id: Int, teks: String, sumber: String, hari: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val updatedQuote = QuotesHarian(id = id, teksQuote = teks, sumber = sumber, hari = hari)
                val response = RetrofitClient.materiApi.updateQuote(id, updatedQuote)
                if (response.isSuccessful) {
                    isActionSuccess = true
                    isQuoteSavedSuccess = true
                    fetchQuotes()
                } else {
                    errorMessage = "Gagal memperbarui quote: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteQuote(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.materiApi.deleteQuote(id)
                if (response.isSuccessful) {
                    fetchQuotes()
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
        }
    }

    // --- LOGIC UNTUK SOAL LATIHAN ---

    fun fetchQuestions() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.latihanApi.getAllSoal()
                if (response.isSuccessful) {
                    _questions.value = response.body() ?: emptyList()
                } else {
                    // Quotes bisa, Latihan Soal gagal? Cek kode error di sini
                    errorMessage = "Error API: ${response.code()}"
                }
            } catch (e: Exception) {
                // Jika mapping jilid_id salah, error akan tertangkap di sini
                errorMessage = "Mapping Error: ${e.localizedMessage}"
                e.printStackTrace() // Cek di Logcat Android Studio
            } finally {
                isLoading = false
            }
        }
    }
    fun saveSoal(soal: LatihanSoal) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.latihanApi.tambahSoalLatihan(soal)
                if (response.isSuccessful) {
                    isActionSuccess = true
                    fetchQuestions()
                } else {
                    errorMessage = "Gagal menyimpan soal: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun updateSoal(id: Int, soal: LatihanSoal) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.latihanApi.updateSoal(id, soal)
                if (response.isSuccessful) {
                    isActionSuccess = true
                    fetchQuestions()
                } else {
                    errorMessage = "Gagal memperbarui soal: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteSoal(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.latihanApi.deleteSoal(id)
                if (response.isSuccessful) {
                    fetchQuestions()
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
        }
    }

    // --- UTILITY ---

    fun resetStatus() {
        isActionSuccess = false
        isQuoteSavedSuccess = false
        errorMessage = null
    }

    fun resetQuoteStatus() = resetStatus()
}