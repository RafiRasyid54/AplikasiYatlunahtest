package com.yatlunah.app.ui.screen.admin_mitra

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// IMPORT MODEL BARU DISINI
import com.yatlunah.app.data.model.UserResponse
import com.yatlunah.app.data.model.GroupListResponse
import com.yatlunah.app.data.model.GroupDetailResponse
import com.yatlunah.app.data.repository.UserRepository
import kotlinx.coroutines.launch

class MitraViewModel : ViewModel() {
    private val repository = UserRepository()

    var userList by mutableStateOf<List<UserResponse>>(emptyList())

    // Sekarang cukup tulis GroupListResponse saja
    var groupList by mutableStateOf<List<GroupListResponse>>(emptyList())

    var selectedGroupStudents by mutableStateOf<List<UserResponse>>(emptyList())
    var selectedTeacherName by mutableStateOf("")

    var isLoading by mutableStateOf(false)

    fun fetchUsersByMitra(role: String, idMitra: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getUsersByMitra(role, idMitra)
                if (response.isSuccessful) {
                    userList = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                userList = emptyList()
            } finally { isLoading = false }
        }
    }

    fun fetchMitraGroups(idMitra: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getMitraGroups(idMitra)
                if (response.isSuccessful) {
                    groupList = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                groupList = emptyList()
            } finally { isLoading = false }
        }
    }

    fun fetchGroupDetails(guruId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getGroupDetails(guruId)
                if (response.isSuccessful) {
                    // Pastikan Response Body dipetakan dengan benar
                    val data = response.body()
                    selectedTeacherName = data?.nama_guru ?: ""
                    selectedGroupStudents = data?.students ?: emptyList()
                }
            } catch (e: Exception) {
                selectedGroupStudents = emptyList()
            } finally { isLoading = false }
        }
    }
}