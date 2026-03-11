package com.yatlunah.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.yatlunah.app.data.model.JilidData
import com.yatlunah.app.data.repository.MateriRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class JilidViewModel : ViewModel() {
    private val repository = MateriRepository()

    private val _jilidList = MutableStateFlow<List<JilidData>>(emptyList())
    val jilidList: StateFlow<List<JilidData>> = _jilidList

    private val _audioProgress = MutableStateFlow(0f)
    val audioProgress: StateFlow<Float> = _audioProgress

    private val _uiState = MutableStateFlow<Map<String, Any>>(emptyMap())
    val uiState: StateFlow<Map<String, Any>> = _uiState

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    // ✅ DEKLARASI MESIN AUDIO (Jangan dihapus)
    private var exoPlayer: ExoPlayer? = null
    private var progressJob: Job? = null

    init {
        fetchJilid()
    }

    private fun fetchJilid() {
        viewModelScope.launch {
            _jilidList.value = repository.getDaftarJilid()
        }
    }

    fun updateProgressToApi(userId: String, jilid: Int, halaman: Int) {
        viewModelScope.launch {
            repository.saveProgress(userId, jilid, halaman)
        }
    }

    // ✅ MENYIAPKAN AUDIO SAAT HALAMAN DIGESER
    fun prepareAudioForPage(context: Context, jilidId: Int, halaman: Int) {
        viewModelScope.launch {
            stopAudio()

            val audioUrl = repository.getAudioUrl(jilidId, halaman)

            if (!audioUrl.isNullOrEmpty()) {
                // Merakit mesin ExoPlayer
                exoPlayer = ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(audioUrl))
                    prepare()

                    addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            if (playbackState == Player.STATE_ENDED) {
                                _isPlaying.value = false
                                _audioProgress.value = 0f
                                progressJob?.cancel()
                            }
                        }
                    })
                }
                println("Audio siap untuk Halaman $halaman")
            } else {
                println("Tidak ada audio untuk Halaman $halaman")
            }
        }
    }

    // ✅ TOMBOL PLAY / PAUSE DITEKAN (Logika Asli)
    fun toggleAudio() {
        exoPlayer?.let { player ->
            if (_isPlaying.value) {
                player.pause()
                _isPlaying.value = false
                progressJob?.cancel()
                println("Audio dijeda")
            } else {
                player.play()
                _isPlaying.value = true
                trackAudioProgress()
                println("Audio diputar")
            }
        }
    }

    // ✅ MENGGERAKKAN GARIS HIJAU PROGRESS AUDIO
    private fun trackAudioProgress() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive && _isPlaying.value) {
                exoPlayer?.let { player ->
                    val durasi = player.duration.toFloat()
                    if (durasi > 0f) {
                        _audioProgress.value = player.currentPosition.toFloat() / durasi
                    }
                }
                delay(100) // Update garis setiap 0.1 detik
            }
        }
    }

    // ✅ BERHENTI PENUH (Saat pindah halaman atau keluar)
    fun stopAudio() {
        _isPlaying.value = false
        _audioProgress.value = 0f
        progressJob?.cancel()
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
    }

    // ✅ BERSIHKAN RAM SAAT KELUAR
    override fun onCleared() {
        super.onCleared()
        stopAudio()
    }

    fun downloadJilid(jilid: JilidData) {
        println("Memulai download: ${jilid.judulJilid}")
    }
}