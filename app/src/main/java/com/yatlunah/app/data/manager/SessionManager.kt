package com.yatlunah.app.data.manager

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("yatlunah_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NAMA = "nama"
        private const val KEY_EMAIL = "email"
        private const val KEY_ROLE = "role"
        private const val KEY_ID_MITRA = "id_mitra"
    }

    // Simpan semua data setelah login sukses
    fun saveSession(userId: String, nama: String, email: String, role: String, idMitra: String?) {
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_NAMA, nama)
            putString(KEY_EMAIL, email)
            putString(KEY_ROLE, role)
            putString(KEY_ID_MITRA, idMitra)
            apply()
        }
    }

    // Ambil data untuk filter API
    fun getRole(): String = prefs.getString(KEY_ROLE, "") ?: ""
    fun getIdMitra(): String? = prefs.getString(KEY_ID_MITRA, null)

    fun logout() {
        prefs.edit().clear().apply()
    }
}