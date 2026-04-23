package com.yatlunah.app.ui.screen.materi

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.media.MediaRecorder
import android.os.Build
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.data.manager.AudioUploadManager
import com.yatlunah.app.data.model.LatihanSoal
import com.yatlunah.app.data.remote.RetrofitClient
import com.yatlunah.app.data.remote.SetoranRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private object ViewerColors {
    val brandGreen     = Color(0xFF00D639)
    val darkBackground = Color(0xFF0F0F0F)
    val darkSurface    = Color(0xFF1A1A1A)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfJilidViewerScreen(
    jilidId: Int,
    userId: String,
    viewModel: JilidViewModel = viewModel(),
    onNavigateToLatihan: (Int, Int) -> Unit, // ✅ Tambahan parameter navigasi (Jilid, Halaman)
    onBack: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uploadManager = remember { AudioUploadManager() }

    val bgColor = if (isDark) ViewerColors.darkBackground else Color(0xFF1A1A1A)
    val surfaceColor = if (isDark) ViewerColors.darkSurface else Color.White
    val textColor = if (isDark) Color.White else Color.Black
    val brandGreen = ViewerColors.brandGreen

    // --- STATES ---
    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var pageCount by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val listJilid by viewModel.jilidList.collectAsState()
    val audioProgress by viewModel.audioProgress.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    var isRecordMode by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var hasRecorded by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var audioFile by remember { mutableStateOf<File?>(null) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }

    // ✅ State Latihan Soal diubah menjadi List
    var daftarSoalAktif by remember { mutableStateOf<List<LatihanSoal>>(emptyList()) }

    var hasMicPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasMicPermission = it }

    // --- PDF ENGINE ---
    LaunchedEffect(jilidId, listJilid) {
        if (listJilid.isEmpty()) return@LaunchedEffect
        withContext(Dispatchers.IO) {
            try {
                isLoading = true
                val currentJilid = listJilid.find { it.nomorJilid == jilidId }
                val pdfUrl = currentJilid?.pdfUrl
                if (pdfUrl.isNullOrEmpty()) {
                    errorMessage = "URL tidak ditemukan"
                    isLoading = false
                    return@withContext
                }

                val tempFile = File(context.cacheDir, "jilid_$jilidId.pdf")
                if (!tempFile.exists() || tempFile.length() == 0L) {
                    java.net.URL(pdfUrl).openStream().use { input ->
                        FileOutputStream(tempFile).use { output -> input.copyTo(output) }
                    }
                }
                val fd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
                pdfRenderer = PdfRenderer(fd)
                pageCount = pdfRenderer?.pageCount ?: 0
                errorMessage = ""
                isLoading = false
            } catch (ex: Exception) {
                errorMessage = ex.localizedMessage ?: "Error memuat file"
                isLoading = false
            }
        }
    }

    val pagerState = rememberPagerState(pageCount = { pageCount })

    // ✅ GABUNGAN: Reset Audio DAN Cek Soal saat pindah halaman
    LaunchedEffect(pagerState.currentPage) {
        val currentHalaman = pagerState.currentPage + 1

        // 1. Reset Audio
        if (!isLoading && pageCount > 0) {
            viewModel.prepareAudioForPage(context, jilidId, currentHalaman)
        }

        // 2. Cek Latihan Soal dari API
        scope.launch {
            try {
                val response = RetrofitClient.latihanApi.getSoalByMapping(jilidId, currentHalaman)
                if (response.isSuccessful) {
                    // Jika list tidak kosong, simpan ke state
                    daftarSoalAktif = response.body() ?: emptyList()
                } else {
                    daftarSoalAktif = emptyList()
                }
            } catch (e: Exception) {
                daftarSoalAktif = emptyList()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            pdfRenderer?.close()
            viewModel.stopAudio()
            mediaRecorder?.apply { try { stop(); release() } catch (e: Exception) {} }
        }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Jilid $jilidId", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Halaman ${pagerState.currentPage + 1} / $pageCount", fontSize = 11.sp, color = Color.LightGray)
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black.copy(alpha = 0.7f))
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = brandGreen)
            } else if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.White, modifier = Modifier.align(Alignment.Center), textAlign = TextAlign.Center)
            } else {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { pageIndex ->
                    var pageBitmap by remember { mutableStateOf<Bitmap?>(null) }
                    LaunchedEffect(pageIndex) {
                        withContext(Dispatchers.IO) {
                            pdfRenderer?.let { renderer ->
                                synchronized(renderer) {
                                    val page = renderer.openPage(pageIndex)
                                    val bitmap = createBitmap((page.width * 1.5).toInt(), (page.height * 1.5).toInt(), Bitmap.Config.ARGB_8888)
                                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                    pageBitmap = bitmap
                                    page.close()
                                }
                            }
                        }
                    }
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f)) {
                            pageBitmap?.let { Image(it.asImageBitmap(), null, modifier = Modifier.fillMaxSize()) }
                        }
                    }
                }

                // ✅ TOMBOL LATIHAN SOAL MUNCUL JIKA ADA SOAL DI HALAMAN INI
                if (daftarSoalAktif.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.TopEnd) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                // Panggil fungsi navigasi, kirimkan jilid dan halaman saat ini
                                onNavigateToLatihan(jilidId, pagerState.currentPage + 1)
                            },
                            containerColor = Color(0xFFD97706), // Warna Amber
                            contentColor = Color.White,
                            icon = { Icon(Icons.Default.Quiz, null) },
                            text = { Text("Latihan Halaman ${pagerState.currentPage + 1}") }
                        )
                    }
                }

                // --- PANEL KONTROL AUDIO & REKAMAN ---
                Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) {
                    // ... (Kode Audio & Record Anda tetap tidak ada yang berubah di sini) ...
                    AnimatedVisibility(visible = !isRecordMode, enter = fadeIn(), exit = fadeOut()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (audioProgress > 0f) {
                                LinearProgressIndicator(
                                    progress = { audioProgress },
                                    modifier = Modifier.width(120.dp).height(4.dp).clip(CircleShape),
                                    color = brandGreen,
                                    trackColor = Color.White.copy(alpha = 0.2f)
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                            Row(modifier = Modifier.background(Color.Black.copy(0.7f), CircleShape).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                FloatingActionButton(
                                    onClick = { viewModel.toggleAudio() },
                                    containerColor = brandGreen,
                                    shape = CircleShape
                                ) {
                                    Icon(imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                }
                                Spacer(Modifier.width(12.dp))
                                FloatingActionButton(
                                    onClick = { isRecordMode = true; viewModel.stopAudio() },
                                    containerColor = Color.White.copy(0.2f),
                                    shape = CircleShape
                                ) {
                                    Icon(Icons.Default.Mic, null, tint = Color.White)
                                }
                            }
                        }
                    }

                    AnimatedVisibility(visible = isRecordMode, enter = slideInVertically { it } + fadeIn(), exit = fadeOut()) {
                        // ... (Logika rekaman Anda) ...
                        Card(colors = CardDefaults.cardColors(containerColor = surfaceColor), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth(0.85f)) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { if (hasRecorded) { hasRecorded = false; audioFile = null } else isRecordMode = false }) {
                                    Icon(if (hasRecorded) Icons.Default.Delete else Icons.Default.Close, null, tint = if (hasRecorded) Color.Red else Color.Gray)
                                }

                                Text(text = if (isUploading) "Mengirim..." else if (hasRecorded) "Siap Kirim" else if (isRecording) "Merekam..." else "Siap Rekam", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = textColor)

                                if (hasRecorded && !isUploading) {
                                    IconButton(
                                        onClick = { /* Logika Kirim Audio */ },
                                        modifier = Modifier.background(brandGreen, CircleShape)
                                    ) { Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White) }
                                } else {
                                    IconButton(
                                        onClick = { /* Logika Record Start/Stop */ },
                                        modifier = Modifier.background(if (isRecording) Color.Red else brandGreen, CircleShape)
                                    ) {
                                        if (isUploading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White)
                                        else Icon(if (isRecording) Icons.Default.Stop else Icons.Default.Mic, null, tint = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}