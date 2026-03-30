package com.yatlunah.app.ui.screen.guru

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.model.Setoran
import com.yatlunah.app.data.model.SetoranPenilaianRequest
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
    // --- 2. KIRIM PENILAIAN (SESUAI TABEL BARU) ---
    fun submitPenilaian(
        setoranId: Int,
        nilai: Int,
        catatan: String,
        idGuru: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // ✅ PERBAIKAN: Gunakan nama parameter yang ada di model (SetoranPenilaianRequest)
                val request = SetoranPenilaianRequest(
                    setoranId = setoranId,      // Gunakan 'setoranId' (bukan setoran_id)
                    nilai = nilai,
                    catatan = catatan,
                    idGuruPenilai = idGuru      // Gunakan 'idGuruPenilai' (bukan id_guru_penilai)
                )

                val response = RetrofitClient.authApi.beriNilaiSetoran(request)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Gagal mengirim nilai: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("GURU_VM", "Error Submit: ${e.message}")
                onError(e.message ?: "Terjadi kesalahan")
            } finally {
                _isLoading.value = false
            }
        }
    }
}