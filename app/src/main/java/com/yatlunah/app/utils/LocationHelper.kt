package com.yatlunah.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationHelper(private val context: Context) {

    // Inisialisasi FusedLocationProviderClient
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onLocationFetched: (Double, Double) -> Unit) {
        val cancellationTokenSource = CancellationTokenSource()

        // Coba ambil lokasi saat ini
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("PRAYER_DEBUG", "Lokasi Baru: ${location.latitude}")
                    onLocationFetched(location.latitude, location.longitude)
                } else {
                    // Jika lokasi null, paksa ambil lokasi terakhir yang tersimpan
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) {
                            Log.d("PRAYER_DEBUG", "Lokasi Terakhir: ${lastLoc.latitude}")
                            onLocationFetched(lastLoc.latitude, lastLoc.longitude)
                        } else {
                            Log.e("PRAYER_DEBUG", "GPS Aktif tapi lokasi tidak ditemukan")
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e("PRAYER_DEBUG", "Gagal total ambil lokasi: ${it.message}")
            }
    }
}