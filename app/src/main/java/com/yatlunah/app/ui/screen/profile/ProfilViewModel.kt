package com.yatlunah.app.ui.screen.profile

import android.util.Log
import androidx.compose.runtime.mutableStateOf // ✅ Tambahkan ini
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.model.UserNameUpdate
import com.yatlunah.app.data.model.UserPasswordUpdate
import com.yatlunah.app.data.model.UserStats // ✅ Import model statistikmu
import com.yatlunah.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ✅ State baru untuk menampung Ringkasan Belajar (Stats)
    var userStats = mutableStateOf<UserStats?>(null)
        private set

    fun setUserData(name: String, email: String) {
        if (_userName.value.isEmpty()) _userName.value = name
        if (_userEmail.value.isEmpty()) _userEmail.value = email
    }

    // ✅ Fungsi baru untuk mengambil Statistik dari Database
    fun fetchUserStats(userId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getUserStats(userId)
                if (response.isSuccessful) {
                    userStats.value = response.body()
                } else {
                    Log.e("YATLUNAH_STATS", "Gagal load stats: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("YATLUNAH_STATS", "Error: ${e.message}")
            }
        }
    }

    fun updateProfileName(userId: String, newName: String, onSuccess: () -> Unit) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.updateName(userId, UserNameUpdate(newName))
                if (response.isSuccessful) {
                    _userName.value = newName
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("PROFILE_ERROR", e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePassword(userId: String, oldPass: String, newPass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = UserPasswordUpdate(
                    old_password = oldPass,
                    new_password = newPass
                )
                val response = repository.updatePassword(userId, request)

                if (response.isSuccessful) {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("YATLUNAH", e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }
}