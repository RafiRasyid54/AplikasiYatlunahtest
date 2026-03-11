package com.yatlunah.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.model.Setoran
import com.yatlunah.app.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GuruViewModel : ViewModel() {

    private val _antreanSetoran = MutableStateFlow<List<Setoran>>(emptyList())
    val antreanSetoran: StateFlow<List<Setoran>> = _antreanSetoran

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchAntrean(jilidId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Pastikan di RetrofitClient variabelnya bernama 'materiApi'
                val response = RetrofitClient.materiApi.getAntreanSetoran(jilidId)
                if (response.isSuccessful) {
                    _antreanSetoran.value = response.body() ?: emptyList()
                    Log.d("GURU_VM", "Berhasil ambil ${_antreanSetoran.value.size} data")
                } else {
                    Log.e("GURU_VM", "Gagal: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("GURU_VM", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}