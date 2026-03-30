package com.yatlunah.app.ui.screen.guru

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruPenilaianDetailScreen(
    setoranId: Int,
    idGuru: String,
    nama: String,
    jilid: Int,
    halaman: Int,
    audioUrl: String, // 👈 Pastikan ini URL lengkap: https://...
    onBack: () -> Unit,
    viewModel: GuruViewModel = viewModel()
) {
    val context = LocalContext.current
    var nilai by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()

    // --- LOGIKA MEDIA PLAYER ---
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) } // Deteksi error audio
    var duration by remember { mutableFloatStateOf(0f) }
    var currentPosition by remember { mutableFloatStateOf(0f) }

    val mediaPlayer = remember { MediaPlayer() }

    DisposableEffect(audioUrl) {
        try {
            isError = false
            isPrepared = false
            mediaPlayer.apply {
                reset()
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                // Pastikan audioUrl tidak kosong dan diawali http
                if (audioUrl.isNotEmpty() && audioUrl.startsWith("http")) {
                    setDataSource(audioUrl)
                    prepareAsync()
                    Log.d("AUDIO_CHECK", "Menyiapkan audio: $audioUrl")
                } else {
                    isError = true
                    Log.e("AUDIO_CHECK", "URL tidak valid: $audioUrl")
                }

                setOnPreparedListener {
                    isPrepared = true
                    duration = it.duration.toFloat()
                    Log.d("AUDIO_CHECK", "Audio Siap!")
                }

                setOnErrorListener { _, what, extra ->
                    isError = true
                    isPrepared = false
                    Log.e("AUDIO_CHECK", "MediaPlayer Error: $what, $extra")
                    true
                }

                setOnCompletionListener {
                    isPlaying = false
                    currentPosition = 0f
                    seekTo(0)
                }
            }
        } catch (e: Exception) {
            isError = true
            Log.e("AUDIO_PLAYER", "Exception: ${e.message}")
        }
        onDispose { mediaPlayer.release() }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = mediaPlayer.currentPosition.toFloat()
            delay(500)
        }
    }

    val brightGreen = Color(0xFF00D639)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Validasi Setoran", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF4F5F7)).padding(innerPadding).padding(20.dp)
        ) {
            // --- INFO SANTRI ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Santri:", fontSize = 12.sp, color = Color.Gray)
                    Text(nama, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Jilid $jilid - Halaman $halaman", fontSize = 14.sp, color = brightGreen)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- AUDIO PLAYER DETAIL ---
            Text("Dengarkan Rekaman", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (isPrepared) {
                                    if (isPlaying) mediaPlayer.pause() else mediaPlayer.start()
                                    isPlaying = !isPlaying
                                }
                            },
                            enabled = isPrepared
                        ) {
                            if (!isPrepared && !isError) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Icon(
                                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    null,
                                    tint = if (isError) Color.Red else Color.White
                                )
                            }
                        }

                        Text(formatTime(currentPosition.toInt()), color = Color.White, fontSize = 11.sp)

                        Slider(
                            value = currentPosition,
                            onValueChange = { currentPosition = it; mediaPlayer.seekTo(it.toInt()) },
                            valueRange = 0f..(if (duration > 0) duration else 1f),
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                            colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = brightGreen)
                        )

                        Text(formatTime(duration.toInt()), color = Color.White, fontSize = 11.sp)
                    }

                    if (isError) {
                        Text("Gagal memuat audio. Cek koneksi atau link Supabase.", color = Color.Red, fontSize = 10.sp, modifier = Modifier.padding(start = 12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- FORM INPUT NILAI ---
            Text("Hasil Koreksi", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = nilai,
                        onValueChange = { if (it.all { c -> c.isDigit() }) nilai = it },
                        label = { Text("Nilai (0-100)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = catatan,
                        onValueChange = { catatan = it },
                        label = { Text("Catatan Feedback") },
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val score = nilai.toIntOrNull()
                    if (score != null && score in 0..100) {
                        viewModel.submitPenilaian(
                            setoranId = setoranId,
                            nilai = score,
                            catatan = catatan,
                            idGuru = idGuru,
                            onSuccess = {
                                Toast.makeText(context, "Nilai Berhasil Dikirim!", Toast.LENGTH_SHORT).show()
                                onBack()
                            },
                            onError = { error ->
                                Toast.makeText(context, "Gagal: $error", Toast.LENGTH_LONG).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Nilai tidak valid", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Kirim Penilaian", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun formatTime(ms: Int): String {
    val totalSecs = ms / 1000
    val mins = totalSecs / 60
    val secs = totalSecs % 60
    return String.format("%02d:%02d", mins, secs)
}