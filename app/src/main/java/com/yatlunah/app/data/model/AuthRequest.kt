package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

//register
data class RegisterRequest(
    val nama_lengkap: String,
    val email: String,
    val password: String
)

// AuthResponse.kt
// Di com.yatlunah.app.data.model.AuthResponse
data class AuthResponse(
    @SerializedName("user_id") // Sesuai return FastAPI kamu: "user_id"
    val userId: String,

    @SerializedName("nama_lengkap") // Sesuai return FastAPI kamu: "nama_lengkap"
    val nama_lengkap: String,

    val email: String, // Pastikan field ini ada
    val role: String,
    val status: String
)

//Login
data class LoginRequest(
    val email: String,
    val password: String
)