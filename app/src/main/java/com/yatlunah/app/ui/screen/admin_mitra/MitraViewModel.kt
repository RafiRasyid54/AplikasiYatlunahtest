package com.yatlunah.app.ui.screen.admin_mitra

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.yatlunah.app.data.model.UserResponse
import com.yatlunah.app.data.model.GroupListResponse
import com.yatlunah.app.data.model.Timings
import com.yatlunah.app.data.repository.UserRepository
import com.yatlunah.app.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MitraViewModel : ViewModel() {
    private val repository = UserRepository()

    var userList by mutableStateOf<List<UserResponse>>(emptyList())
    var groupList by mutableStateOf<List<GroupListResponse>>(emptyList())
    var selectedGroupStudents by mutableStateOf<List<UserResponse>>(emptyList())
    var selectedTeacherName by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    // --- State Informasi & Statistik Lembaga ---
    var namaLembaga by mutableStateOf("Memuat Lembaga...")
    var totalGuruLembaga by mutableIntStateOf(0)
    var totalSantriLembaga by mutableIntStateOf(0)
    var totalUserLembaga by mutableIntStateOf(0)

    // --- State Lokasi & Waktu Shalat ---
    var hijriDate by mutableStateOf("Memuat tanggal...")
    var currentLocationName by mutableStateOf("Mencari lokasi...")
    var currentShalatName by mutableStateOf("-")
    var currentShalatTime by mutableStateOf("-")

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    // ====================================================================
    // FUNGSI LOKASI & SHALAT
    // ====================================================================
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
            Log.e("MitraVM", "Gagal update lokasi: ${exception.message}")
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
                    Log.e("MitraVM", "Geocoder error: ${e.message}")
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
                Log.e("MitraVM", "API Shalat Error: ${e.message}")
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

    // ====================================================================
    // FUNGSI DATA MITRA
    // ====================================================================
    fun fetchDashboardData(idMitra: String) {
        if (idMitra == "0" || idMitra.isBlank()) {
            namaLembaga = "Lembaga Pusat"
            return
        }

        viewModelScope.launch {
            try {
                val infoDeferred = async { RetrofitClient.adminApi.getMitraInfo(idMitra) }
                val statsDeferred = async { RetrofitClient.adminApi.getMitraStatistik(idMitra) }

                val infoResponse = infoDeferred.await()
                val statsResponse = statsDeferred.await()

                if (infoResponse.isSuccessful) {
                    namaLembaga = infoResponse.body()?.nama_lembaga ?: "Lembaga Tidak Diketahui"
                }

                if (statsResponse.isSuccessful) {
                    val stats = statsResponse.body()
                    totalGuruLembaga = stats?.total_guru ?: 0
                    totalSantriLembaga = stats?.total_santri ?: 0
                    totalUserLembaga = stats?.total_user ?: 0
                }
            } catch (e: Exception) {
                namaLembaga = "Koneksi Error"
            }
        }
    }

    // ... sisa fungsi fetchUsersByMitra, fetchMitraGroups, dll tetap sama
    fun fetchUsersByMitra(role: String, idMitra: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getUsersByMitra(role, idMitra)
                if (response.isSuccessful) userList = response.body() ?: emptyList()
            } catch (e: Exception) { userList = emptyList() } finally { isLoading = false }
        }
    }

    fun fetchMitraGroups(idMitra: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getMitraGroups(idMitra)
                if (response.isSuccessful) groupList = response.body() ?: emptyList()
            } catch (e: Exception) { groupList = emptyList() } finally { isLoading = false }
        }
    }

    fun fetchGroupDetails(guruId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getGroupDetails(guruId)
                if (response.isSuccessful) {
                    val data = response.body()
                    selectedTeacherName = data?.nama_guru ?: ""
                    selectedGroupStudents = data?.students ?: emptyList()
                }
            } catch (e: Exception) { selectedGroupStudents = emptyList() } finally { isLoading = false }
        }
    }
}