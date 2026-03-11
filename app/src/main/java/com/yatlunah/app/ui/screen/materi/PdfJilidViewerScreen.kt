package com.yatlunah.app.ui.screen.materi

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.media.MediaRecorder
import android.os.Build
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.data.manager.AudioUploadManager
import com.yatlunah.app.data.remote.RetrofitClient
import com.yatlunah.app.data.remote.SetoranRequest
import com.yatlunah.app.ui.viewmodel.JilidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfJilidViewerScreen(
    jilidId: Int,
    userId: String,
    viewModel: JilidViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uploadManager = remember { AudioUploadManager() }

    // --- STATES ---
    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var pageCount by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val listJilid by viewModel.jilidList.collectAsState()
    val audioProgress by viewModel.audioProgress.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    // State Rekaman
    var isRecordMode by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var hasRecorded by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var audioFile by remember { mutableStateOf<File?>(null) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }

    // --- 1. LOGIC DOWNLOAD & INISIALISASI PDF RENDERER ---
    LaunchedEffect(jilidId, listJilid) {
        if (listJilid.isEmpty()) return@LaunchedEffect
        withContext(Dispatchers.IO) {
            try {
                val currentJilid = listJilid.find { it.nomorJilid == jilidId }
                val pdfUrl = currentJilid?.pdfUrl ?: throw Exception("URL PDF kosong")

                val tempFile = File(context.cacheDir, "temp_jilid_$jilidId.pdf")
                if (!tempFile.exists()) {
                    java.net.URL(pdfUrl).openStream().use { input ->
                        FileOutputStream(tempFile).use { output -> input.copyTo(output) }
                    }
                }

                val fd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(fd)
                pdfRenderer = renderer
                pageCount = renderer.pageCount
                isLoading = false
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Gagal memuat PDF"
                isLoading = false
            }
        }
    }

    val pagerState = rememberPagerState(pageCount = { pageCount })

    // --- 2. LOGIC TRIGGER AUDIO PER HALAMAN ---
    LaunchedEffect(pagerState.currentPage) {
        if (!isLoading && pageCount > 0) {
            val page = pagerState.currentPage + 1
            viewModel.prepareAudioForPage(context, jilidId, page)

            // Reset state rekaman saat pindah halaman
            isRecordMode = false
            isRecording = false
            hasRecorded = false
            audioFile = null
        }
    }

    // Dispose renderer saat keluar dari screen agar memori bebas
    DisposableEffect(Unit) {
        onDispose {
            pdfRenderer?.close()
            viewModel.stopAudio()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Jilid $jilidId", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Halaman ${pagerState.currentPage + 1}", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFF2B2B2B))) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF00D639))
            } else if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red, modifier = Modifier.align(Alignment.Center))
            } else {
                // --- 3. PAGER DENGAN RENDER ON-DEMAND (ANTI-ANR) ---
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { pageIndex ->
                    var pageBitmap by remember { mutableStateOf<Bitmap?>(null) }

                    // Render hanya halaman yang sedang ditampilkan
                    LaunchedEffect(pageIndex) {
                        withContext(Dispatchers.IO) {
                            pdfRenderer?.let { renderer ->
                                val page = renderer.openPage(pageIndex)
                                // Ukuran asli agar ringan di RAM
                                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                pageBitmap = bitmap
                                page.close()
                            }
                        }
                    }

                    Card(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        pageBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        } ?: Box(Modifier.fillMaxSize()) {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }
                    }
                }

                // --- 4. PANEL KONTROL (AUDIO & RECORD) ---
                Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 30.dp), contentAlignment = Alignment.Center) {

                    // MODE NORMAL (AUDIO USTADZ)
                    AnimatedVisibility(visible = !isRecordMode, enter = fadeIn(), exit = fadeOut()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (audioProgress > 0f) {
                                LinearProgressIndicator(
                                    progress = { audioProgress },
                                    modifier = Modifier.width(150.dp).height(4.dp).clip(CircleShape).padding(bottom = 8.dp),
                                    color = Color(0xFF00D639)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                                FloatingActionButton(onClick = { viewModel.toggleAudio() }, containerColor = Color(0xFF00D639)) {
                                    Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = Color.White)
                                }
                                FloatingActionButton(onClick = { isRecordMode = true; viewModel.stopAudio() }, containerColor = Color(0xFF007BFF)) {
                                    Icon(Icons.Default.Mic, null, tint = Color.White)
                                }
                            }
                        }
                    }

                    // MODE REKAM (SETORAN SANTRI)
                    AnimatedVisibility(visible = isRecordMode, enter = fadeIn(), exit = fadeOut()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(32.dp),
                            modifier = Modifier.fillMaxWidth(0.9f).padding(16.dp)
                        ) {
                            if (isUploading) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                    CircularProgressIndicator(Modifier.size(20.dp)); Spacer(Modifier.width(12.dp)); Text("Mengirim...")
                                }
                            } else {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                    IconButton(onClick = {
                                        if (hasRecorded) { hasRecorded = false; audioFile = null } else isRecordMode = false
                                    }) {
                                        Icon(if (hasRecorded) Icons.Default.Delete else Icons.Default.Close, null, tint = if (hasRecorded) Color.Red else Color.Gray)
                                    }

                                    Text(if (hasRecorded) "Siap Kirim" else if (isRecording) "Merekam..." else "Siap Rekam", fontWeight = FontWeight.Bold)

                                    if (hasRecorded) {
                                        IconButton(onClick = {
                                            scope.launch {
                                                isUploading = true
                                                val fileName = "setoran_${userId}_${System.currentTimeMillis()}.m4a"

                                                // 1. Proses Upload ke Supabase
                                                val url = audioFile?.let { uploadManager.uploadAudio(it, fileName) }

                                                if (url != null) {
                                                    // DEBUG: Cek apakah URL-nya muncul di Logcat
                                                    println("RAFI_DEBUG: URL Supabase didapat -> $url")

                                                    try {
                                                        // 2. LAPOR KE FASTAPI (Bagian ini yang kemungkinan gagal)
                                                        val response = RetrofitClient.materiApi.tambahSetoran(
                                                            SetoranRequest(
                                                                userId = userId,
                                                                jilidId = jilidId,
                                                                halaman = pagerState.currentPage + 1,
                                                                audioUrl = url  // Link dari Supabase tadi
                                                            )
                                                        )

                                                        if (response.isSuccessful) {
                                                            Toast.makeText(context, "Setoran Berhasil Masuk Database!", Toast.LENGTH_SHORT).show()
                                                            isRecordMode = false
                                                            hasRecorded = false
                                                        } else {
                                                            // Cek error dari FastAPI (biasanya 422 atau 500)
                                                            val errorBody = response.errorBody()?.string()
                                                            println("RAFI_DEBUG_ERROR: FastAPI menolak data -> $errorBody")
                                                            Toast.makeText(context, "Database Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                                                        }
                                                    } catch (e: Exception) {
                                                        println("RAFI_DEBUG_ERROR: Gagal konek ke FastAPI -> ${e.message}")
                                                        Toast.makeText(context, "Gagal lapor ke server!", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                isUploading = false
                                            }
                                        }) {
                                            Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color(0xFF007BFF))
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                if (isRecording) {
                                                    mediaRecorder?.apply { stop(); release() }
                                                    mediaRecorder = null
                                                    isRecording = false; hasRecorded = true
                                                } else {
                                                    val file = File(context.cacheDir, "rec_${System.currentTimeMillis()}.m4a")
                                                    audioFile = file
                                                    val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(context) else MediaRecorder()
                                                    mediaRecorder = recorder.apply {
                                                        setAudioSource(MediaRecorder.AudioSource.MIC)
                                                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                                                        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                                                        setOutputFile(file.absolutePath)
                                                        prepare(); start()
                                                    }
                                                    isRecording = true
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (isRecording) Color.Red else Color(0xFF00D639)),
                                            shape = CircleShape
                                        ) {
                                            Icon(if (isRecording) Icons.Default.Stop else Icons.Default.Mic, null)
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
}