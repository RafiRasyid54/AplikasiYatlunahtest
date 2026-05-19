package com.yatlunah.app.ui.screen.admin

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.yatlunah.app.data.model.QuotesHarian
import com.yatlunah.app.data.model.LatihanSoal
import com.yatlunah.app.data.model.Timings
import com.yatlunah.app.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

// Model sementara untuk Log Aktivitas (Nanti bisa dipindahkan ke folder model)
data class ActivityLog(val nama: String, val aksi: String, val waktu: String, val initials: String, val role: String = "admin")

class AdminViewModel : ViewModel() {

    // --- STATE MANAGEMENT (QUOTES & SOAL) ---
    private val _quotes = MutableStateFlow<List<QuotesHarian>>(emptyList())
    val quotes: StateFlow<List<QuotesHarian>> = _quotes.asStateFlow()

    private val _questions = MutableStateFlow<List<LatihanSoal>>(emptyList())
    val questions: StateFlow<List<LatihanSoal>> = _questions.asStateFlow()

    // --- STATE OVERVIEW STATISTIK (REALTIME) ---
    var totalPengguna by mutableStateOf(0)
    var totalGuru by mutableStateOf(0)
    var totalSantri by mutableStateOf(0)
    var totalMitra by mutableStateOf(0) // ✅ TAMBAHKAN INI

    // --- STATE WAKTU SHALAT & LOKASI (REALTIME) ---
    var hijriDate by mutableStateOf("Memuat tanggal...")
    var currentLocationName by mutableStateOf("Mencari lokasi...")
    var currentShalatName by mutableStateOf("-")
    var currentShalatTime by mutableStateOf("-")

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    // --- STATE LOG AKTIVITAS ---
    var recentLogs by mutableStateOf<List<ActivityLog>>(emptyList())

    // --- STATE UI UMUM ---
    var isLoading by mutableStateOf(false)
    var isActionSuccess by mutableStateOf(false)
    var isQuoteSavedSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        fetchQuotes()
        fetchQuestions()
        fetchDashboardStats()
        fetchRecentLogs()
    }

    // =========================================================================
    // 1. LOGIC LOKASI & WAKTU SHALAT (Sama dengan SantriViewModel)
    // =========================================================================

    @SuppressLint("MissingPermission")
    fun startRealtimeUpdates(context: Context) {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 600000)
            .setMinUpdateIntervalMillis(300000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    updateAddressName(context, location.latitude, location.longitude)
                    fetchPrayerFromApi(location.latitude, location.longitude)
                }
            }
        }

        try {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                context.mainLooper
            )
        } catch (exception: Exception) {
            Log.e("AdminVM", "Gagal update lokasi: ${exception.message}")
        }
    }

    private fun updateAddressName(context: Context, lat: Double, lon: Double) {
        val geocoder = Geocoder(context, Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lon, 1) { addresses ->
                if (addresses.isNotEmpty()) {
                    currentLocationName = addresses[0].locality ?: addresses[0].subAdminArea ?: "Lokasi Terdeteksi"
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    withContext(Dispatchers.Main) {
                        if (!addresses.isNullOrEmpty()) {
                            currentLocationName = addresses[0].locality ?: addresses[0].subAdminArea ?: "Lokasi Terdeteksi"
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AdminVM", "Geocoder error: ${e.message}")
                }
            }
        }
    }

    private fun fetchPrayerFromApi(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.prayerApi.getPrayerTimings(lat, lon)
                if (response.isSuccessful) {
                    val prayerData = response.body()?.data
                    withContext(Dispatchers.Main) {
                        prayerData?.let {
                            hijriDate = "${it.date.hijri.date} ${it.date.hijri.month.en} ${it.date.hijri.year} H"
                            updateNextPrayer(it.timings)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AdminVM", "API Shalat Error: ${e.message}")
            }
        }
    }

    private fun updateNextPrayer(timings: Timings) {
        val nowStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        when {
            nowStr < timings.fajr -> { currentShalatName = "Shubuh"; currentShalatTime = timings.fajr }
            nowStr < timings.dhuhr -> { currentShalatName = "Dzuhur"; currentShalatTime = timings.dhuhr }
            nowStr < timings.asr -> { currentShalatName = "Ashar"; currentShalatTime = timings.asr }
            nowStr < timings.maghrib -> { currentShalatName = "Maghrib"; currentShalatTime = timings.maghrib }
            nowStr < timings.isha -> { currentShalatName = "Isya"; currentShalatTime = timings.isha }
            else -> { currentShalatName = "Shubuh"; currentShalatTime = timings.fajr }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
    }

    // =========================================================================
    // 2. LOGIC STATISTIK OVERVIEW & LOG AKTIVITAS
    // =========================================================================

    fun fetchDashboardStats() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Memanggil API langsung ke backend
                val response = RetrofitClient.adminApi.getDashboardStats()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val stats = response.body()
                        if (stats != null) {
                            totalPengguna = stats.totalUser
                            totalGuru = stats.totalGuru
                            totalSantri = stats.totalSantri
                            totalMitra = stats.totalMitra // ✅ TAMBAHKAN INI
                        }
                    } else {
                        errorMessage = "Gagal mengambil statistik: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Error koneksi API Statistik: ${e.message}"
                }
            }
        }
    }

    fun fetchRecentLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // TODO: Hubungkan ke endpoint API log (misal: RetrofitClient.adminApi.getLogs())
                // Sementara menggunakan dummy data yang akan me-render list pada layar
                withContext(Dispatchers.Main) {
                    recentLogs = listOf(
                        ActivityLog("Admin Rafi", "Menambah Guru Baru", "10 menit lalu", "AR", "admin"),
                        ActivityLog("Ustadz Budi", "Menilai Setoran Jilid 2", "1 jam lalu", "UB", "guru"),
                        ActivityLog("Santri Aisyah", "Melakukan Registrasi", "3 jam lalu", "SA", "santri")
                    )
                }
            } catch (e: Exception) {
                errorMessage = "Gagal memuat log: ${e.message}"
            }
        }
    }

    // =========================================================================
    // 3. LOGIC UNTUK QUOTE (Sama dengan Aslinya)
    // =========================================================================

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

    // =========================================================================
    // 4. LOGIC UNTUK SOAL LATIHAN (Sama dengan Aslinya)
    // =========================================================================

    fun fetchQuestions() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.latihanApi.getAllSoal()
                if (response.isSuccessful) {
                    _questions.value = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error API: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Mapping Error: ${e.localizedMessage}"
                e.printStackTrace()
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

    // =========================================================================
    // 5. UTILITY
    // =========================================================================

    fun resetStatus() {
        isActionSuccess = false
        isQuoteSavedSuccess = false
        errorMessage = null
    }

    fun resetQuoteStatus() = resetStatus()
}