package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

// --- REQUEST MODELS ---
data class RegisterRequest(
    val nama_lengkap: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

// --- RESPONSE MODEL (Gunakan Satu untuk Semua) ---
data class AuthResponse(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("nama_lengkap")
    val nama_lengkap: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("status")
    val status: String
)

data class UserResponse(
    @SerializedName("user_id") val userId: String,
    @SerializedName("nama_lengkap") val nama_lengkap: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String
)