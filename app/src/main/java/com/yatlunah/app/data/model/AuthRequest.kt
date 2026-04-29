package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

// --- REQUEST MODELS ---
data class RegisterRequest(
    val nama_lengkap: String,
    val email: String,
    val password: String,
    val role: String = "santri", // Default role
    val id_mitra: String? = null // Diisi jika didaftarkan oleh Admin Mitra
)

data class LoginRequest(
    val email: String,
    val password: String
)

// --- RESPONSE MODEL (Gunakan Satu untuk Semua) ---
data class AuthResponse(
    @SerializedName("user_id") val userId: String,
    @SerializedName("nama_lengkap") val nama_lengkap: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("id_mitra") val idMitra: String?,
    @SerializedName("status") val status: String
)

data class UserResponse(
    @SerializedName("user_id") val userId: String,
    @SerializedName("nama_lengkap") val nama_lengkap: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("id_mitra") val idMitra: String?
)

// Simpan di data/model/MessageResponse.kt
data class MessageResponse(
    val status: String,
    val message: String? = null
)