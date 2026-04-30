package com.yatlunah.app.ui.screen.materi

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.yatlunah.app.data.model.JilidData
import com.yatlunah.app.data.remote.RetrofitClient
import com.yatlunah.app.data.repository.MateriRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class JilidViewModel : ViewModel() {
    private val repository = MateriRepository()

    private val _jilidList = MutableStateFlow<List<JilidData>>(emptyList())
    val jilidList: StateFlow<List<JilidData>> = _jilidList

    private val _audioProgress = MutableStateFlow(0f)
    val audioProgress: StateFlow<Float> = _audioProgress

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    // State untuk Player & Recorder
    private var exoPlayer: ExoPlayer? = null
    private var progressJob: Job? = null
    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String? = null

    init {
        fetchJilid()
    }

    private fun fetchJilid() {
        viewModelScope.launch {
            _jilidList.value = repository.getDaftarJilid()
        }
    }

    // --- LOGIKA PEREKAMAN (RECORDER) ---

    fun startRecording(context: Context) {
        val file = File(context.cacheDir, "temp_setoran.mp3")
        audioFilePath = file.absolutePath

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFilePath)
            try {
                prepare()
                start()
                Log.d("YATLUNAH_AUDIO", "Perekaman dimulai: $audioFilePath")
            } catch (e: IOException) {
                Log.e("YATLUNAH_AUDIO", "Gagal prepare recorder: ${e.message}")
            }
        }
    }

    fun stopRecording(): File? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            if (audioFilePath != null) File(audioFilePath!!) else null
        } catch (e: Exception) {
            Log.e("YATLUNAH_AUDIO", "Gagal stop recorder: ${e.message}")
            null
        }
    }

    fun cancelRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        } catch (e: Exception) { }
        mediaRecorder = null
        audioFilePath?.let { File(it).delete() }
        audioFilePath = null
    }

    // --- LOGIKA UNGGAH KE DATABASE ---[cite: 1, 2]

    suspend fun uploadSetoran(
        context: Context,
        userId: String,
        jilid: Int,
        halaman: Int,
        audioFile: File
    ): Boolean {
        return try {
            // Mengonversi file menjadi MultipartBody
            val requestFile = audioFile.asRequestBody("audio/mpeg".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", audioFile.name, requestFile)

            // Mengonversi metadata menjadi RequestBody[cite: 2]
            val userIdPart = userId.toRequestBody("text/plain".toMediaTypeOrNull())
            val jilidPart = jilid.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val halamanPart = halaman.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = RetrofitClient.materiApi.uploadSetoran(
                file = filePart,
                userId = userIdPart,
                jilid = jilidPart,
                halaman = halamanPart
            )

            response.isSuccessful
        } catch (e: Exception) {
            Log.e("YATLUNAH_API", "Error upload: ${e.message}")
            false
        }
    }

    // --- LOGIKA AUDIO PLAYER (EXOPLAYER) ---

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

    fun downloadJilid(jilid: JilidData) {
        // Implementasi sementara agar build berhasil
        println("Memulai download jilid: ${jilid.judulJilid}")
        // Anda bisa menambahkan logika DownloadManager di sini nanti
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

    override fun onCleared() {
        super.onCleared()
        stopAudio()
        cancelRecording()
    }
}