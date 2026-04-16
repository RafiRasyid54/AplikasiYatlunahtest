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

    // --- PERMISSION ---
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

    // Reset Audio saat pindah halaman
    LaunchedEffect(pagerState.currentPage) {
        if (!isLoading && pageCount > 0) {
            viewModel.prepareAudioForPage(context, jilidId, pagerState.currentPage + 1)
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

                // --- PANEL KONTROL ---
                Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) {

                    // Mode Playback (Audio Ustadz)
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
                                    onClick = {
                                        // Pastikan audio di-stop dulu jika sedang record, baru nyalakan playback
                                        viewModel.toggleAudio()
                                    },
                                    containerColor = brandGreen,
                                    shape = CircleShape
                                ) {
                                    // Ikon berubah reaktif mengikuti State di ViewModel
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                FloatingActionButton(
                                    onClick = {
                                        isRecordMode = true
                                        viewModel.stopAudio() // Wajib stop audio ustadz saat mau rekam
                                    },
                                    containerColor = Color.White.copy(0.2f),
                                    shape = CircleShape
                                ) {
                                    Icon(Icons.Default.Mic, null, tint = Color.White)
                                }
                            }
                        }
                    }

                    // Mode Rekam (Setoran Santri)
                    AnimatedVisibility(visible = isRecordMode, enter = slideInVertically { it } + fadeIn(), exit = fadeOut()) {
                        Card(colors = CardDefaults.cardColors(containerColor = surfaceColor), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth(0.85f)) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    if (hasRecorded) { hasRecorded = false; audioFile = null } else isRecordMode = false
                                }) {
                                    Icon(if (hasRecorded) Icons.Default.Delete else Icons.Default.Close, null, tint = if (hasRecorded) Color.Red else Color.Gray)
                                }

                                Text(
                                    text = if (isUploading) "Mengirim..." else if (hasRecorded) "Siap Kirim" else if (isRecording) "Merekam..." else "Siap Rekam",
                                    fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = textColor
                                )

                                if (hasRecorded && !isUploading) {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                isUploading = true
                                                val fileName = "setoran_${userId}_${System.currentTimeMillis()}.m4a"
                                                val url = audioFile?.let { uploadManager.uploadAudio(it, fileName) }
                                                if (url != null) {
                                                    val res = RetrofitClient.materiApi.tambahSetoran(SetoranRequest(userId, jilidId, pagerState.currentPage + 1, url))
                                                    if (res.isSuccessful) {
                                                        Toast.makeText(context, "Berhasil!", Toast.LENGTH_SHORT).show()
                                                        isRecordMode = false; hasRecorded = false
                                                    }
                                                }
                                                isUploading = false
                                            }
                                        },
                                        modifier = Modifier.background(brandGreen, CircleShape)
                                    ) { Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White) }
                                } else {
                                    IconButton(
                                        onClick = {
                                            if (!hasMicPermission) { launcher.launch(Manifest.permission.RECORD_AUDIO); return@IconButton }
                                            if (isRecording) {
                                                try {
                                                    mediaRecorder?.apply { stop(); reset(); release() }
                                                    mediaRecorder = null
                                                    isRecording = false
                                                    val size = audioFile?.length() ?: 0L
                                                    if (size > 0) hasRecorded = true else Toast.makeText(context, "Gagal merekam!", Toast.LENGTH_SHORT).show()
                                                } catch (e: Exception) { e.printStackTrace() }
                                            } else {
                                                val file = File(context.cacheDir, "rec_${System.currentTimeMillis()}.m4a")
                                                audioFile = file
                                                mediaRecorder = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(context) else MediaRecorder()).apply {
                                                    setAudioSource(MediaRecorder.AudioSource.MIC)
                                                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                                                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                                                    setOutputFile(file.absolutePath)
                                                    prepare(); start()
                                                }
                                                isRecording = true
                                                hasRecorded = false
                                            }
                                        },
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