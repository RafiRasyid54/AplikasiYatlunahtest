package com.yatlunah.app.ui.screen.materi

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatlunah.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Buat data class sederhana untuk UI State Audio
data class AudioUiState(
    val audioResId: Int? = null,
    val isPlaying: Boolean = false
)

class MateriViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow<AudioUiState?>(AudioUiState())
    val uiState: StateFlow<AudioUiState?> = _uiState

    private val _audioProgress = MutableStateFlow(0f)
    val audioProgress: StateFlow<Float> = _audioProgress

    // ✅ 1. Perbaikan: Ganti nama fungsi agar sinkron dengan Screen
    fun updateProgressToApi(userId: String, jilid: Int, halaman: Int) {
        viewModelScope.launch {
            try {
                val response = repository.updateProgress(userId, jilid, halaman)
                if (response.isSuccessful) {
                    Log.d("YATLUNAH_DEBUG", "Progress Saved: Jilid $jilid Hal $halaman")
                }
            } catch (e: Exception) {
                // ✅ Variabel 'e' sekarang digunakan untuk logging agar tidak warning
                Log.e("YATLUNAH_DEBUG", "Gagal update progress: ${e.message}")
            }
        }
    }

    // ✅ 2. Perbaikan: Fungsi Audio agar tidak Unused
    fun toggleAudio(context: Context) {
        val currentState = _uiState.value
        if (currentState?.isPlaying == true) {
            stopAudio()
        } else {
            // Simulasi play audio (nanti sesuaikan dengan MediaPlayer kamu)
            _uiState.value = currentState?.copy(isPlaying = true)
            Log.d("YATLUNAH_DEBUG", "Playing audio in context: $context")
        }
    }

    fun stopAudio() {
        _uiState.value = _uiState.value?.copy(isPlaying = false)
        _audioProgress.value = 0f
    }

    // ✅ 3. Fungsi lama onPageChanged dihapus atau dialihkan ke updateProgressToApi
    // Jika di Screen kamu memanggil onPageChanged, ubah panggilannya menjadi updateProgressToApi
}