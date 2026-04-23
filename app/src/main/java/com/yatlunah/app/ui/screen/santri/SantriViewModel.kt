package com.yatlunah.app.ui.screen.santri

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.yatlunah.app.data.remote.RetrofitClient
import com.yatlunah.app.data.repository.AuthRepository
import com.yatlunah.app.data.model.Timings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class SantriViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    // --- State Real-Time ---
    var hijriDate by mutableStateOf("Memuat tanggal...")
    var currentShalatTime by mutableStateOf("--:--")
    var currentShalatName by mutableStateOf("Shalat")
    var currentLocationName by mutableStateOf("Mencari lokasi...")

    // --- State Bimbingan (DISESUAIKAN DENGAN MODEL ANDA) ---
    var bimbinganStatus by mutableStateOf("")
    // Properti ini harus ada karena dipanggil di MainActivity/Dashboard
    var namaGuru by mutableStateOf("")

    // --- State Statistik & Quote ---
    var streak by mutableStateOf("0 Hari")
    var progressPercent by mutableFloatStateOf(0f)
    var lastRead by mutableStateOf("Jilid 1")
    var lastPage by mutableStateOf("Hal. 0")
    var currentQuote by mutableStateOf("Memuat inspirasi hari ini...")
    var currentSource by mutableStateOf("") // State baru untuk sumbe
    var isLoading by mutableStateOf(false)

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

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
            Log.e("SantriVM", "Gagal update lokasi: ${exception.message}")
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
                    Log.e("SantriVM", "Geocoder error: ${e.message}")
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
                Log.e("SantriVM", "API Shalat Error: ${e.message}")
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

    // --- FUNGSI BIMBINGAN (DISINKRONKAN DENGAN MODEL ANDA) ---
    fun fetchStatusBimbingan(userId: String) {
        if (userId == "guest_user") return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.bimbinganApi.getBimbinganStatusSantri(userId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val dataList = response.body()?.data
                        val data = dataList?.firstOrNull()
                        if (data != null) {
                            bimbinganStatus = data.status
                            // Karena di model Anda adanya 'namaSantri', kita gunakan itu
                            // atau default "Ustadz/ah" agar variabel namaGuru terisi
                            namaGuru = data.namaSantri ?: "Ustadz/ah"
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SantriVM", "Bimbingan error: ${e.message}")
            }
        }
    }

    fun fetchStats(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            try {
                val response = authRepository.getUserStats(userId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        streak = "${data?.currentStreak ?: 0} Hari"
                        progressPercent = data?.totalProgress ?: 0f
                        lastRead = "Jilid ${data?.lastJilid ?: 1}"
                        lastPage = "Halaman ${data?.lastHalaman ?: 0}"
                    }
                }
            } catch (e: Exception) {
                Log.e("SantriVM", "Stats error: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    fun fetchQuoteBerdasarkanHari() {
        viewModelScope.launch(Dispatchers.IO) {
            val hariIni = SimpleDateFormat("EEEE", Locale.forLanguageTag("id-ID")).format(Date())
            try {
                val response = RetrofitClient.materiApi.getQuotesByHari(hariIni)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        // Ambil teks DAN sumbernya
                        currentQuote = data?.teksQuote ?: "Semangat Belajar!"
                        currentSource = data?.sumber ?: "Yatlunah"
                    }
                }
            } catch (e: Exception) {
                Log.e("SantriVM", "Quote error: ${e.message}")
                withContext(Dispatchers.Main) {
                    currentQuote = "Semangat Belajar!"
                    currentSource = "Yatlunah"
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
    }
}