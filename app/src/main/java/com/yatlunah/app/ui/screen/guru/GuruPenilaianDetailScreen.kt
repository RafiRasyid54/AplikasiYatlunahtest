package com.yatlunah.app.ui.screen.guru

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.yatlunah.app.ui.screen.guru.GuruViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruPenilaianDetailScreen(
    setoranId: Int,      // ✅ Tambahan: ID unik dari tabel setoran
    idGuru: String,      // ✅ Tambahan: ID Guru yang sedang login
    nama: String,
    jilid: Int,
    halaman: Int,
    audioUrl: String,
    onBack: () -> Unit,
    viewModel: GuruViewModel = viewModel() // ✅ Hubungkan ke ViewModel
) {
    val context = LocalContext.current
    var nilai by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()

    // --- LOGIKA MEDIA PLAYER ---
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }
    var duration by remember { mutableFloatStateOf(0f) }
    var currentPosition by remember { mutableFloatStateOf(0f) }
    val mediaPlayer = remember { MediaPlayer() }

    DisposableEffect(audioUrl) {
        try {
            mediaPlayer.apply {
                reset()
                setAudioAttributes(AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA).build())
                if (audioUrl.isNotEmpty()) {
                    setDataSource(audioUrl)
                    prepareAsync()
                }
                setOnPreparedListener {
                    isPrepared = true
                    duration = it.duration.toFloat()
                }
                setOnCompletionListener {
                    isPlaying = false
                    currentPosition = 0f
                    seekTo(0)
                }
            }
        } catch (e: Exception) { Log.e("AUDIO_PLAYER", "Error: ${e.message}") }
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
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (isPrepared) {
                            if (isPlaying) mediaPlayer.pause() else mediaPlayer.start()
                            isPlaying = !isPlaying
                        }
                    }) {
                        Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = Color.White)
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

            // --- TOMBOL SUBMIT (SINKRON DENGAN VIEWMODEL) ---
            Button(
                onClick = {
                    val score = nilai.toIntOrNull()
                    if (score != null && score in 0..100) {
                        // ✅ Panggil fungsi submit dari ViewModel
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
                enabled = !isLoading, // Disable saat loading
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

// ✅ FUNGSI HELPER (Taruh di luar Composable tapi di dalam file yang sama)
private fun formatTime(ms: Int): String {
    val totalSecs = ms / 1000
    val mins = totalSecs / 60
    val secs = totalSecs % 60
    return String.format("%02d:%02d", mins, secs)
}