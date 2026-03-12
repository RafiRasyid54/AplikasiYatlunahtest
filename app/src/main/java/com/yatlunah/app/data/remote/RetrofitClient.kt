package com.yatlunah.app.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
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
}