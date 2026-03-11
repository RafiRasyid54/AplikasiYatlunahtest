package com.yatlunah.app.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Alamat 10.0.2.2 agar emulator bisa akses localhost laptop kamu
    private const val BASE_URL = "http://10.0.2.2:8000/"

    // 1. Kita buat mesin Retrofit utamanya terpisah agar bisa dipakai banyak API
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 2. Pertahankan 'instance' untuk AuthApiService agar kodingan Login kamu tidak error
    val instance: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    // 3. ✅ Tambahkan 'materiApi' khusus untuk urusan Jilid, PDF, dan Audio
    val materiApi: MateriApiService by lazy {
        retrofit.create(MateriApiService::class.java)
    }
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
}