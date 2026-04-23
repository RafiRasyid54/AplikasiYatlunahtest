package com.yatlunah.app.data.remote

import com.yatlunah.app.data.remote.LatihanApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://yatlunahbackend-production.up.railway.app/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // ✅ Tambahkan client agar tidak timeout saat upload/download
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ✅ Tambahkan 'authApi' (untuk manajemen user & login)
    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    // ✅ Tambahkan 'materiApi' (untuk setoran & jilid)
    val materiApi: MateriApiService by lazy {
        retrofit.create(MateriApiService::class.java)
    }

    // 🔗 'instance' diarahkan ke authApi agar kode lama kamu tidak error
    val instance: AuthApiService get() = authApi


    // ✅ TAMBAHKAN INI: 'bimbinganApi' (untuk fitur approval guru & antrean)
    val bimbinganApi: BimbinganApiService by lazy {
        retrofit.create(BimbinganApiService::class.java)
    }

    // Di dalam object RetrofitClient
    val prayerApi: PrayerApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.aladhan.com/") // URL dasar Aladhan
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PrayerApiService::class.java)
    }

    val latihanApi: LatihanApiService by lazy {
        retrofit.create(LatihanApiService::class.java)
    }
}

