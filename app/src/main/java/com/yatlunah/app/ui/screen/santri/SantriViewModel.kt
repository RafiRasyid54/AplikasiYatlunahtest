package com.yatlunah.app.ui.screen.santri

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.repository.AuthRepository
import com.yatlunah.app.data.remote.RetrofitClient // ✅ Tambahkan import ini
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SantriViewModel : ViewModel() {
    private val repository = AuthRepository()

    // --- State Statistik & Quote ---
    var streak by mutableStateOf("0 Hari")
    var lastRead by mutableStateOf("Jilid 1")
    var lastPage by mutableStateOf("Hal. 0")
    var progressPercent by mutableFloatStateOf(0f)
    var isLoading by mutableStateOf(false)
    var currentQuote by mutableStateOf("Memuat inspirasi...")
    var currentSource by mutableStateOf("")

    // ✅ --- STATE BARU: STATUS BIMBINGAN ---
    var bimbinganStatus by mutableStateOf("")
    var namaGuru by mutableStateOf("")

    // ✅ FUNGSI BARU: AMBIL STATUS DARI DATABASE
    fun fetchStatusBimbingan(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.bimbinganApi.getBimbinganStatusSantri(userId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val bimbinganAktif = body?.data?.firstOrNull()

                        if (bimbinganAktif != null) {
                            // ✅ Jika ada data, update status sesuai database
                            bimbinganStatus = bimbinganAktif.status
                            namaGuru = bimbinganAktif.namaSantri ?: "Ustadz/ah"
                        } else {
                            // ✅ JIKA DATA NULL (BELUM DAFTAR), PAKSA STATUS JADI KOSONG
                            bimbinganStatus = ""
                            namaGuru = ""
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SantriVM", "Error fetch bimbingan: ${e.message}")
                // ✅ Jika error (misal koneksi mati), anggap belum daftar agar tombol muncul
                withContext(Dispatchers.Main) { bimbinganStatus = "" }
            }
        }
    }

    fun fetchStats(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            try {
                val response = repository.getUserStats(userId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        streak = "${data?.currentStreak ?: 0} Hari"
                        lastRead = "Jilid ${data?.lastJilid ?: 1}"
                        lastPage = "Halaman ${data?.lastHalaman ?: 0}"
                        progressPercent = data?.totalProgress ?: 0f
                    }
                }
            } catch (e: Exception) {
                Log.e("DashboardVM", "Error: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    fun fetchQuoteBerdasarkanHari() {
        viewModelScope.launch(Dispatchers.IO) {
            // PERBAIKAN LOCALE: Gunakan Locale.forLanguageTag atau constructor yang benar
            val localeId = Locale("id", "ID")
            val sdf = SimpleDateFormat("EEEE", localeId)
            val hariIni = sdf.format(Date())

            try {
                val response = RetrofitClient.materiApi.getQuotesByHari(hariIni)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        // PERBAIKAN REFERENCE: Sesuaikan dengan model (teksQuote & sumber)
                        currentQuote = data?.teksQuote ?: "Tetap semangat belajar Al-Qur'an!"
                        currentSource = data?.sumber ?: "Yatlunah"
                    }
                }
            } catch (e: Exception) {
                Log.e("SantriVM", "Error: ${e.message}")
            }
        }
    }
}