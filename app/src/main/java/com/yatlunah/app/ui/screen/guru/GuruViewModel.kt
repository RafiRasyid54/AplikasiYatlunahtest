package com.yatlunah.app.ui.screen.guru

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.model.Setoran
import com.yatlunah.app.data.remote.PenilaianRequest
import com.yatlunah.app.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GuruViewModel : ViewModel() {

    private val _antreanSetoran = MutableStateFlow<List<Setoran>>(emptyList())
    val antreanSetoran: StateFlow<List<Setoran>> = _antreanSetoran

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // --- 1. AMBIL ANTREAN ---
    fun fetchAntrean(jilidId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.materiApi.getAntreanSetoran(jilidId)
                if (response.isSuccessful) {
                    // ✅ UBAH "menunggu" JADI "pending" agar sinkron dengan FastAPI
                    val listAntrean = response.body()?.filter { it.status == "pending" } ?: emptyList()

                    _antreanSetoran.value = listAntrean
                    Log.d("GURU_VM", "Berhasil ambil ${listAntrean.size} antrean")
                } else {
                    Log.e("GURU_VM", "Gagal Fetch: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("GURU_VM", "Error Fetch: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- 2. KIRIM PENILAIAN (SESUAI TABEL BARU) ---
    fun submitPenilaian(
        setoranId: Int,
        nilai: Int,
        catatan: String,
        idGuru: String, // Diambil dari ID Login Guru
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = PenilaianRequest(
                    setoranId = setoranId,
                    nilai = nilai,
                    catatan = catatan,
                    idGuru = idGuru // Sesuai kolom id_guru_penilai
                )

                val response = RetrofitClient.materiApi.updateNilaiSetoran(request)

                if (response.isSuccessful) {
                    Log.d("GURU_VM", "Penilaian Berhasil Dikirim")
                    onSuccess()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Gagal mengirim nilai"
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                onError("Koneksi Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}