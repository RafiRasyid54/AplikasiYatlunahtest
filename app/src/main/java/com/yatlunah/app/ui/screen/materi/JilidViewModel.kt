package com.yatlunah.app.ui.screen.materi

import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
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

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

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

    /**
     * Menyimpan progres belajar ke database
     * Dipanggil otomatis saat santri membuka halaman tertentu.
     */
    fun updateProgressToApi(userId: String, jilid: Int, halaman: Int) {
        viewModelScope.launch {
            try {
                repository.saveProgress(userId, jilid, halaman)
            } catch (e: Exception) {
                println("Gagal update progres: ${e.message}")
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun prepareAudioForPage(context: Context, jilidId: Int, halaman: Int) {
        viewModelScope.launch {
            stopAudio()
            val audioUrl = repository.getAudioUrl(jilidId, halaman)

            if (!audioUrl.isNullOrEmpty()) {
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
            }
        }
    }

    fun toggleAudio() {
        exoPlayer?.let { player ->
            if (_isPlaying.value) {
                player.pause()
                _isPlaying.value = false
                progressJob?.cancel()
            } else {
                player.play()
                _isPlaying.value = true
                trackAudioProgress()
            }
        }
    }

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
                delay(100)
            }
        }
    }

    fun stopAudio() {
        _isPlaying.value = false
        _audioProgress.value = 0f
        progressJob?.cancel()
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
    }

    // Tambahkan ini di dalam class JilidViewModel
    fun downloadJilid(jilid: JilidData) {
        // Fitur ini akan dikembangkan di tahap selanjutnya
        println("Memulai download jilid: ${jilid.judulJilid}")
    }

    override fun onCleared() {
        super.onCleared()
        stopAudio()
    }
}