package com.yatlunah.app.ui.screen.materi

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
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
import androidx.core.graphics.createBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.data.model.LatihanSoal
import com.yatlunah.app.data.model.Setoran
import com.yatlunah.app.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

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
    onNavigateToLatihan: (Int, Int) -> Unit,
    onBack: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val bgColor = if (isDark) ViewerColors.darkBackground else Color(0xFF1A1A1A)
    val surfaceColor = if (isDark) ViewerColors.darkSurface else Color.White
    val textColor = if (isDark) Color.White else Color.Black

    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var pageCount by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val listJilid by viewModel.jilidList.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    // State Rekaman Baru (Satu kali rekam = Satu setoran)[cite: 1]
    var isRecordMode by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var audioFile by remember { mutableStateOf<File?>(null) }

    var daftarSoalAktif by remember { mutableStateOf<List<LatihanSoal>>(emptyList()) }
    var listSetoranHalaman by remember { mutableStateOf<List<Setoran>>(emptyList()) }
    var showFeedbackSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // 1. Memuat File PDF & Renderer[cite: 2]
    LaunchedEffect(jilidId, listJilid) {
        if (listJilid.isEmpty()) return@LaunchedEffect
        withContext(Dispatchers.IO) {
            try {
                isLoading = true
                val currentJilid = listJilid.find { it.nomorJilid == jilidId }
                val pdfUrl = currentJilid?.pdfUrl
                if (pdfUrl.isNullOrEmpty()) {
                    errorMessage = "URL PDF tidak ditemukan"
                    return@withContext
                }

                val tempFile = File(context.cacheDir, "jilid_$jilidId.pdf")
                if (!tempFile.exists() || tempFile.length() == 0L) {
                    URL(pdfUrl).openStream().use { input ->
                        FileOutputStream(tempFile).use { output -> input.copyTo(output) }
                    }
                }

                val fd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
                pdfRenderer = PdfRenderer(fd)
                pageCount = pdfRenderer?.pageCount ?: 0
            } catch (ex: Exception) {
                errorMessage = "Gagal memuat PDF: ${ex.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    val pagerState = rememberPagerState(pageCount = { pageCount })

    // 2. Memuat Audio & Data per Halaman[cite: 2]
    LaunchedEffect(pagerState.currentPage, isLoading) {
        if (isLoading || pageCount == 0) return@LaunchedEffect

        val currentHalaman = pagerState.currentPage + 1

        // Beri jeda agar inisialisasi audio stabil dan tidak crash[cite: 2]
        delay(150)
        viewModel.prepareAudioForPage(context, jilidId, currentHalaman)

        scope.launch {
            try {
                val resSoal = RetrofitClient.latihanApi.getSoalByMapping(jilidId, currentHalaman)
                daftarSoalAktif = if (resSoal.isSuccessful) resSoal.body() ?: emptyList() else emptyList()

                val resSetoran = RetrofitClient.materiApi.getRiwayatSetoran(userId)
                if (resSetoran.isSuccessful) {
                    listSetoranHalaman = resSetoran.body()?.filter { s ->
                        s.jilid == jilidId && s.halaman == currentHalaman && s.status == "dinilai"
                    } ?: emptyList()
                }
            } catch (e: Exception) {
                daftarSoalAktif = emptyList()
                listSetoranHalaman = emptyList()
            }
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
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.stopAudio()
                        onBack()
                    }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.7f),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = ViewerColors.brandGreen)
            } else if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.White, modifier = Modifier.align(Alignment.Center), textAlign = TextAlign.Center)
            } else {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { pageIndex ->
                    var pageBitmap by remember { mutableStateOf<Bitmap?>(null) }

                    LaunchedEffect(pageIndex) {
                        withContext(Dispatchers.IO) {
                            pdfRenderer?.let { renderer ->
                                synchronized(renderer) {
                                    try {
                                        val page = renderer.openPage(pageIndex)
                                        val bitmap = createBitmap((page.width * 1.5).toInt(), (page.height * 1.5).toInt(), Bitmap.Config.ARGB_8888)
                                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                        pageBitmap = bitmap
                                        page.close()
                                    } catch (e: Exception) {}
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f)
                        ) {
                            pageBitmap?.let {
                                Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }

                // Bagian Panel Rekam dan Aksi[cite: 1]
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ActionButtonsRow(
                        isRecordMode = isRecordMode,
                        isPlaying = isPlaying,
                        hasFeedback = listSetoranHalaman.isNotEmpty(),
                        hasLatihan = daftarSoalAktif.isNotEmpty(),
                        onToggleAudio = { viewModel.toggleAudio() },
                        onToggleRecord = {
                            isRecordMode = !isRecordMode
                            if (isRecordMode) {
                                viewModel.stopAudio()
                                audioFile = null
                            }
                        },
                        onShowFeedback = { showFeedbackSheet = true },
                        onGoToLatihan = {
                            if (daftarSoalAktif.isNotEmpty()) {
                                viewModel.stopAudio()
                                onNavigateToLatihan(jilidId, pagerState.currentPage + 1)
                            } else {
                                Toast.makeText(context, "Latihan belum tersedia", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    AnimatedVisibility(
                        visible = isRecordMode,
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut()
                    ) {
                        RecordPanel(
                            isRecording = isRecording,
                            hasRecording = audioFile != null,
                            surfaceColor = surfaceColor,
                            textColor = textColor,
                            onRecordClick = {
                                if (!isRecording) {
                                    isRecording = true
                                    audioFile = null
                                    viewModel.startRecording(context) // Hubungkan ke fungsi rekam[cite: 1]
                                } else {
                                    isRecording = false
                                    audioFile = viewModel.stopRecording() // Ambil file hasil rekam[cite: 1]
                                }
                            },
                            onSend = {
                                scope.launch {
                                    if (audioFile != null) {
                                        // Memanggil fungsi upload yang sebenarnya ke database[cite: 1, 2]
                                        val success = viewModel.uploadSetoran(
                                            context = context,
                                            userId = userId,
                                            jilid = jilidId,
                                            halaman = pagerState.currentPage + 1,
                                            audioFile = audioFile!!
                                        )
                                        if (success) {
                                            Toast.makeText(context, "Setoran terkirim!", Toast.LENGTH_SHORT).show()
                                            isRecordMode = false
                                            audioFile = null
                                        } else {
                                            Toast.makeText(context, "Gagal mengirim setoran", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            onCancel = {
                                isRecordMode = false
                                isRecording = false
                                audioFile = null
                                viewModel.cancelRecording()
                            }
                        )
                    }
                }
            }
        }

        if (showFeedbackSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFeedbackSheet = false },
                sheetState = sheetState,
                containerColor = surfaceColor
            ) {
                FeedbackListContent(
                    halaman = pagerState.currentPage + 1,
                    listSetoran = listSetoranHalaman
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopAudio() }
    }
}

@Composable
fun ActionButtonsRow(
    isRecordMode: Boolean,
    isPlaying: Boolean,
    hasFeedback: Boolean,
    hasLatihan: Boolean,
    onToggleAudio: () -> Unit,
    onToggleRecord: () -> Unit,
    onShowFeedback: () -> Unit,
    onGoToLatihan: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color.Black.copy(0.8f), RoundedCornerShape(32.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onToggleAudio) {
            Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = Color.White)
        }
        IconButton(onClick = onToggleRecord) {
            Icon(Icons.Default.Mic, null, tint = if (isRecordMode) Color.Red else Color.White)
        }
        IconButton(onClick = onShowFeedback) {
            Icon(Icons.AutoMirrored.Filled.Comment, null, tint = if (hasFeedback) ViewerColors.brandGreen else Color.Gray)
        }
        Button(
            onClick = onGoToLatihan,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hasLatihan) Color(0xFFD97706) else Color.Gray.copy(0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Quiz, null, modifier = Modifier.size(16.dp), tint = Color.White)
            Spacer(Modifier.width(4.dp))
            Text("Latihan", fontSize = 12.sp, color = Color.White)
        }
    }
}

@Composable
fun RecordPanel(
    isRecording: Boolean,
    hasRecording: Boolean,
    surfaceColor: Color,
    textColor: Color,
    onRecordClick: () -> Unit,
    onSend: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onCancel) { Icon(Icons.Default.Close, null, tint = Color.Gray) }

            Text(
                text = if (isRecording) "Sedang Merekam..." else if (hasRecording) "Siap Kirim" else "Tekan Mic",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            if (hasRecording && !isRecording) {
                IconButton(
                    onClick = onSend,
                    modifier = Modifier.background(ViewerColors.brandGreen, CircleShape)
                ) { Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White) }
            } else {
                IconButton(
                    onClick = onRecordClick,
                    modifier = Modifier.background(if (isRecording) Color.Red else ViewerColors.brandGreen, CircleShape)
                ) { Icon(if (isRecording) Icons.Default.Stop else Icons.Default.Mic, null, tint = Color.White) }
            }
        }
    }
}

@Composable
fun FeedbackListContent(halaman: Int, listSetoran: List<Setoran>) {
    Column(modifier = Modifier.padding(24.dp).fillMaxHeight(0.6f)) {
        Text("Komentar Hal $halaman", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))
        if (listSetoran.isEmpty()) {
            Text("Belum ada feedback.", color = Color.Gray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(listSetoran) { s ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(0.1f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Nilai: ${s.nilai ?: "-"}", fontWeight = FontWeight.Bold, color = ViewerColors.brandGreen)
                                Text(s.createdAt.take(10), fontSize = 10.sp, color = Color.Gray)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(s.catatan ?: "Tidak ada catatan.", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}