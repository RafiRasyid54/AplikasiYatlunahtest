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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruPenilaianDetailScreen(
    nama: String,
    jilid: Int,
    halaman: Int,
    audioUrl: String,
    onBack: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    val context = LocalContext.current
    var nilai by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }

    // --- LOGIKA MEDIA PLAYER DENGAN SEEKBAR ---
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }
    var duration by remember { mutableFloatStateOf(0f) }
    var currentPosition by remember { mutableFloatStateOf(0f) }
    val mediaPlayer = remember { MediaPlayer() }

    // Inisialisasi Player
    DisposableEffect(audioUrl) {
        try {
            mediaPlayer.apply {
                reset()
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
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
        } catch (e: Exception) {
            Log.e("AUDIO_PLAYER", "Error: ${e.message}")
        }
        onDispose { mediaPlayer.release() }
    }

    // Update Seekbar tiap 500ms saat memutar
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
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F5F7))
                .padding(innerPadding)
                .padding(20.dp)
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

            // --- AUDIO PLAYER DETAIL (SEPERTI GAMBAR) ---
            Text("Dengarkan Rekaman", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF333333)) // Warna gelap
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (isPrepared) {
                                if (isPlaying) mediaPlayer.pause() else mediaPlayer.start()
                                isPlaying = !isPlaying
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    // Durasi Berjalan
                    Text(
                        text = formatTime(currentPosition.toInt()),
                        color = Color.White,
                        fontSize = 11.sp
                    )

                    // Seekbar / Slider
                    Slider(
                        value = currentPosition,
                        onValueChange = {
                            currentPosition = it
                            mediaPlayer.seekTo(it.toInt())
                        },
                        valueRange = 0f..(if (duration > 0) duration else 1f),
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = brightGreen,
                            inactiveTrackColor = Color.Gray
                        )
                    )

                    // Total Durasi
                    Text(
                        text = formatTime(duration.toInt()),
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- FORM INPUT NILAI ---
            Text("Hasil Koreksi", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = nilai,
                        onValueChange = { if (it.length <= 3 && it.all { c -> c.isDigit() }) nilai = it },
                        label = { Text("Nilai (0-100)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = catatan,
                        onValueChange = { catatan = it },
                        label = { Text("Catatan Feedback") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val score = nilai.toIntOrNull()
                    if (score != null && score in 0..100) onConfirm(score, catatan)
                    else Toast.makeText(context, "Nilai tidak valid", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, null)
                Spacer(Modifier.width(8.dp))
                Text("Kirim Penilaian", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Helper untuk format waktu 00:00
fun formatTime(ms: Int): String {
    val totalSecs = ms / 1000
    val mins = totalSecs / 60
    val secs = totalSecs % 60
    return String.format("%02d:%02d", mins, secs)
}