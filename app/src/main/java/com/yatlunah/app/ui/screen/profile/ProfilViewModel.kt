// ✅ PERBAIKAN: Hapus titik di akhir (Tadi kamu tulis 'profile.')
package com.yatlunah.app.ui.screen.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// ✅ PERBAIKAN: Import model yang benar (Sesuaikan dengan folder model kamu)
import com.yatlunah.app.data.model.UserNameUpdate
import com.yatlunah.app.data.model.UserPasswordUpdate
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

    fun setUserData(name: String, email: String) {
        if (_userName.value.isEmpty()) _userName.value = name
        if (_userEmail.value.isEmpty()) _userEmail.value = email
    }

    fun updateProfileName(userId: String, newName: String, onSuccess: () -> Unit) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Pastikan repository sudah punya fungsi ini
                val response = repository.updateProfileName(userId, UserNameUpdate(newName))
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

    // Di ProfileViewModel.kt
    fun updatePassword(userId: String, oldPass: String, newPass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // ✅ Membuat objek request dengan data dari UI
                val request = UserPasswordUpdate(old_password = oldPass, new_password = newPass)
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