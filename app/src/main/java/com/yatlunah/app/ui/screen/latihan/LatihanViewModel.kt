package com.yatlunah.app.ui.screen.latihan

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.model.LatihanSoal
import com.yatlunah.app.data.model.ProgresLatihanRequest // Pastikan model ini sudah dibuat
import com.yatlunah.app.data.remote.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LatihanUiState(
    val currentIndex: Int = 0,
    val jawabanBenarCount: Int = 0,
    val skorAkhir: Int = 0,
    val jawabanTerpilih: String? = null,
    val isSelesai: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

class LatihanViewModel : ViewModel() {
    private val _uiState = mutableStateOf(LatihanUiState())
    val uiState: State<LatihanUiState> = _uiState

    val daftarSoal = mutableStateListOf<LatihanSoal>()

    // Variabel bantuan untuk menyimpan konteks sesi
    private var currentJilidId: Int = 0
    private var currentHalaman: Int = 0
    private var userId: String = ""

    fun fetchSoalSesi(jilid: Int, halaman: Int, uid: String) {
        // Simpan ke memori agar bisa digunakan saat kuis selesai
        currentJilidId = jilid
        currentHalaman = halaman
        userId = uid

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            try {
                val response = RetrofitClient.latihanApi.getSoalByMapping(jilid, halaman)
                if (response.isSuccessful && response.body() != null) {
                    daftarSoal.clear()
                    daftarSoal.addAll(response.body()!!)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Tidak ada latihan di halaman ini.")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Error: ${e.message}")
            }
        }
    }

    fun pilihJawaban(jawaban: String) {
        if (_uiState.value.jawabanTerpilih != null) return

        val soalSaatIni = daftarSoal[_uiState.value.currentIndex]
        val isBenar = jawaban == soalSaatIni.kunciJawaban

        _uiState.value = _uiState.value.copy(
            jawabanTerpilih = jawaban,
            jawabanBenarCount = if (isBenar) _uiState.value.jawabanBenarCount + 1 else _uiState.value.jawabanBenarCount
        )

        viewModelScope.launch {
            delay(1500)
            if (_uiState.value.currentIndex < daftarSoal.size - 1) {
                _uiState.value = _uiState.value.copy(
                    currentIndex = _uiState.value.currentIndex + 1,
                    jawabanTerpilih = null
                )
            } else {
                val totalSoal = daftarSoal.size
                val hitungSkor = if (totalSoal > 0) (_uiState.value.jawabanBenarCount.toFloat() / totalSoal * 100).toInt() else 0

                _uiState.value = _uiState.value.copy(isSelesai = true, skorAkhir = hitungSkor)

                // ✅ PANGGIL FUNGSI SIMPAN DI SINI
                simpanProgresKeServer(hitungSkor)
            }
        }
    }

    private fun simpanProgresKeServer(skor: Int) {
        viewModelScope.launch {
            try {
                val request = ProgresLatihanRequest(
                    userId = userId,
                    jilidId = currentJilidId,
                    halamanLatihan = currentHalaman,
                    skor = skor
                )
                val response = RetrofitClient.latihanApi.simpanProgresLatihan(request)
                if (response.isSuccessful) {
                    Log.d("LatihanProgres", "Data berhasil masuk ke Supabase!")
                } else {
                    Log.e("LatihanProgres", "Gagal menyimpan: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("LatihanProgres", "Exception: ${e.message}")
            }
        }
    }
}