package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

// --- REQUEST MODELS ---
data class RegisterRequest(
    val nama_lengkap: String,
    val email: String,
    val password: String,
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
    val status: String,

    // TAMBAHKAN BARIS INI
    // Pastikan nilai @SerializedName cocok dengan key JSON dari backend Anda (misal: "id_mitra" atau "idMitra")
    @SerializedName("id_mitra")
    val idMitra: String? = null
)

data class UserResponse(
    @SerializedName("user_id") // Sesuai dengan primary key di tabel 'users' Anda
    val userId: String,

    @SerializedName("nama_lengkap")
    val nama_lengkap: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("id_mitra") // Kunci untuk pembagian kelompok
    val id_mitra: String? = null
)

// Simpan di data/model/MessageResponse.kt
data class MessageResponse(
    val status: String,
    val message: String? = null
)