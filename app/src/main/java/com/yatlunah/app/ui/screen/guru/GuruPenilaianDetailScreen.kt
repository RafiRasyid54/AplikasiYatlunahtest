package com.yatlunah.app.ui.screen.guru

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────
// Token Warna Identik
// ─────────────────────────────────────────────
private object PenilaianColors {
    val brandGreen     = Color(0xFF00D639)
    val brandBlue      = Color(0xFF007BFF)
    val darkBackground = Color(0xFF0F0F0F)
    val darkSurface    = Color(0xFF1A1A1A)
    val lightBackground = Color(0xFFF4F5F7)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruPenilaianDetailScreen(
    setoranId: Int,
    idGuru: String,
    nama: String,
    jilid: Int,
    halaman: Int,
    audioUrl: String,
    onBack: () -> Unit,
    viewModel: GuruViewModel = viewModel()
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    // Theme Logic
    val bgColor = if (isDark) PenilaianColors.darkBackground else PenilaianColors.lightBackground
    val surfaceColor = if (isDark) PenilaianColors.darkSurface else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)
    val brandGreen = PenilaianColors.brandGreen

    var nilai by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()

    // --- LOGIKA MEDIA PLAYER ---
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var duration by remember { mutableFloatStateOf(0f) }
    var currentPosition by remember { mutableFloatStateOf(0f) }
    val mediaPlayer = remember { MediaPlayer() }

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
                if (audioUrl.isNotEmpty() && audioUrl.startsWith("http")) {
                    setDataSource(audioUrl)
                    prepareAsync()
                } else {
                    isError = true
                }
                setOnPreparedListener {
                    isPrepared = true
                    duration = it.duration.toFloat()
                }
                setOnErrorListener { _, _, _ -> isError = true; true }
                setOnCompletionListener {
                    isPlaying = false
                    currentPosition = 0f
                    seekTo(0)
                }
            }
        } catch (e: Exception) { isError = true }
        onDispose { mediaPlayer.release() }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = mediaPlayer.currentPosition.toFloat()
            delay(500)
        }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Validasi Setoran", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = textColor)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = surfaceColor)
                )
                HorizontalDivider(color = if (isDark) Color(0xFF2E2E2E) else Color(0xFFE5E5E5), thickness = 0.5.dp)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // --- HEADER INFO ---
            Text(
                text = "INFORMASI SANTRI",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(nama, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        color = brandGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Jilid $jilid • Halaman $halaman",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = brandGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- AUDIO PLAYER (APPLE STYLE) ---
            Text("REKAMAN SUARA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF252525) else Color(0xFF333333))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = brandGreen.copy(alpha = if (isPrepared) 1f else 0.1f)
                        ) {
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
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                } else {
                                    Icon(
                                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        null,
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Slider(
                                value = currentPosition,
                                onValueChange = { currentPosition = it; mediaPlayer.seekTo(it.toInt()) },
                                valueRange = 0f..(if (duration > 0) duration else 1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = brandGreen,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                                )
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(formatTime(currentPosition.toInt()), color = Color.LightGray, fontSize = 10.sp)
                                Text(formatTime(duration.toInt()), color = Color.LightGray, fontSize = 10.sp)
                            }
                        }
                    }
                    AnimatedVisibility(visible = isError) {
                        Text("Gagal memuat rekaman. Pastikan koneksi stabil.", color = Color(0xFFFF453A), fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- FORM INPUT ---
            Text("FORM PENILAIAN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = nilai,
                        onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 3) nilai = it },
                        label = { Text("Nilai (0-100)") },
                        placeholder = { Text("Contoh: 85") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = brandGreen,
                            focusedLabelColor = brandGreen
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = catatan,
                        onValueChange = { catatan = it },
                        label = { Text("Catatan / Perbaikan") },
                        placeholder = { Text("Tulis umpan balik untuk santri...") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = brandGreen,
                            focusedLabelColor = brandGreen
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- SUBMIT BUTTON ---
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
                                Toast.makeText(context, "Koreksi Terkirim!", Toast.LENGTH_SHORT).show()
                                onBack()
                            },
                            onError = { Toast.makeText(context, "Gagal: $it", Toast.LENGTH_LONG).show() }
                        )
                    } else {
                        Toast.makeText(context, "Masukkan nilai 0-100", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PenilaianColors.brandBlue)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Konfirmasi Penilaian", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private fun formatTime(ms: Int): String {
    val totalSecs = ms / 1000
    val mins = totalSecs / 60
    val secs = totalSecs % 60
    return String.format("%02d:%02d", mins, secs)
}