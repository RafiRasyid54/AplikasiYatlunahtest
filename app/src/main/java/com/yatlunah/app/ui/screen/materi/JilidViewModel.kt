package com.yatlunah.app.ui.screen.materi

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.yatlunah.app.data.SupabaseConfig
import com.yatlunah.app.data.model.JilidData
import com.yatlunah.app.data.remote.RetrofitClient
import com.yatlunah.app.data.remote.SetoranRequest
import com.yatlunah.app.data.repository.MateriRepository
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class JilidViewModel : ViewModel() {

    private val repository = MateriRepository()
    private val _jilidList = MutableStateFlow<List<JilidData>>(emptyList())
    val jilidList: StateFlow<List<JilidData>> = _jilidList

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private var exoPlayer: ExoPlayer? = null
    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String? = null
    private var currentLoadedAudioUrl: String? = null

    init { fetchJilid() }

    private fun fetchJilid() {
        viewModelScope.launch { _jilidList.value = repository.getDaftarJilid() }
    }

    // --- LOGIKA PLAYER AUDIO (DIPERBAIKI UNTUK GOOGLE DRIVE) ---
    @OptIn(UnstableApi::class)
    fun prepareAudioForPage(context: Context, jilidId: Int, halaman: Int) {
        viewModelScope.launch {
            val audioUrl = repository.getAudioUrl(jilidId, halaman)

            Log.d("AUDIO_PLAYER", "Mencari audio untuk Jilid $jilidId Halaman $halaman")
            Log.d("AUDIO_PLAYER", "URL: $audioUrl")

            if (audioUrl.isNullOrEmpty()) {
                Log.e("AUDIO_PLAYER", "URL Kosong di database!")
                stopAudio()
                currentLoadedAudioUrl = null
                return@launch
            }

            if (audioUrl == currentLoadedAudioUrl && exoPlayer != null) return@launch

            withContext(Dispatchers.Main) {
                stopAudio()
                try {
                    // Gunakan DataSource khusus untuk menangani redirect Google Drive
                    val dataSourceFactory = DefaultHttpDataSource.Factory()
                        .setAllowCrossProtocolRedirects(true)
                        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")

                    val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(audioUrl))

                    exoPlayer = ExoPlayer.Builder(context).build().apply {
                        setMediaSource(mediaSource)
                        prepare()

                        // Aktifkan Auto-Play
                        playWhenReady = true
                        _isPlaying.value = true

                        addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(state: Int) {
                                if (state == Player.STATE_ENDED) _isPlaying.value = false
                            }
                            override fun onPlayerError(error: PlaybackException) {
                                Log.e("AUDIO_PLAYER", "Gagal putar (Cek Quota Google Drive): ${error.message}")
                            }
                        })
                    }
                    currentLoadedAudioUrl = audioUrl
                } catch (e: Exception) {
                    Log.e("AUDIO_PLAYER", "Crash ExoPlayer: ${e.message}")
                }
            }
        }
    }

    fun toggleAudio() {
        exoPlayer?.let { player ->
            if (_isPlaying.value) {
                player.pause()
                _isPlaying.value = false
            } else {
                player.play()
                _isPlaying.value = true
            }
        }
    }

    fun stopAudio() {
        viewModelScope.launch(Dispatchers.Main) {
            _isPlaying.value = false
            exoPlayer?.stop()
            exoPlayer?.release()
            exoPlayer = null
        }
    }

    // --- LOGIKA PEREKAMAN ---
    fun startRecording(context: Context) {
        val file = File(context.cacheDir, "temp_setoran.mp3")
        audioFilePath = file.absolutePath

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION") MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFilePath)
            try {
                prepare()
                start()
            } catch (e: Exception) {
                Log.e("REKAMAN", "Gagal rekam: ${e.message}")
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
            audioFilePath?.let { File(it) }
        } catch (_: Exception) { null }
    }

    fun cancelRecording() {
        mediaRecorder?.release()
        mediaRecorder = null
        audioFilePath?.let { File(it).delete() }
    }

    // --- LOGIKA UPLOAD ---
    suspend fun uploadSetoran(userId: String, jilid: Int, halaman: Int, audioFile: File): Boolean {
        return try {
            val fileName = "setoran_${userId}_${System.currentTimeMillis()}.mp3"

            withContext(Dispatchers.IO) {
                SupabaseConfig.client.storage[SupabaseConfig.bucketName].upload(
                    path = fileName,
                    data = audioFile.readBytes(),
                    upsert = true
                )
            }

            val fullAudioUrl = "${SupabaseConfig.PROJECT_URL}/storage/v1/object/public/${SupabaseConfig.bucketName}/$fileName"

            val request = SetoranRequest(
                userId = userId,
                jilid = jilid,
                halaman = halaman,
                audioUrl = fullAudioUrl
            )

            val response = RetrofitClient.materiApi.tambahSetoran(request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("API_ERROR", "Exception: ${e.message}")
            false
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAudio()
    }
}