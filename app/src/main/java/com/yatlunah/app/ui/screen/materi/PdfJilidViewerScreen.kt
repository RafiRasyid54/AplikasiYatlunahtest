package com.yatlunah.app.ui.screen.materi

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import com.yatlunah.app.ui.screen.materi.MateriViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfJilidViewerScreen(
    jilidId: Int,
    pdfFileName: String,
    userId: String, // ✅ Diambil dari Navigasi MainActivity
    viewModel: MateriViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val audioProgress by viewModel.audioProgress.collectAsState()

    var bitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Memuat PDF
    LaunchedEffect(pdfFileName) {
        withContext(Dispatchers.IO) {
            try {
                val cleanFileName = pdfFileName.substringAfterLast("/")
                val file = File(context.cacheDir, cleanFileName)
                context.assets.open(pdfFileName).use { input ->
                    FileOutputStream(file).use { output -> input.copyTo(output) }
                }
                val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(fd)
                val tempBitmaps = mutableListOf<Bitmap>()
                for (i in 0 until renderer.pageCount) {
                    val page = renderer.openPage(i)
                    val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    tempBitmaps.add(bitmap)
                    page.close()
                }
                renderer.close()
                fd.close()
                bitmaps = tempBitmaps
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
            }
        }
    }

    val pagerState = rememberPagerState(pageCount = { bitmaps.size })

    // ✅ SIMPAN PROGRESS OTOMATIS KE FASTAPI
    // Setiap kali halaman berubah, kita panggil API update_progress
    LaunchedEffect(pagerState.currentPage) {
        if (!isLoading && bitmaps.isNotEmpty()) {
            // Kita kirim: userId asli, jilidId, dan halaman (currentPage + 1)
            viewModel.updateProgressToApi(
                userId = userId,
                jilid = jilidId,
                halaman = pagerState.currentPage + 1
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Materi Jilid $jilidId", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        if (!isLoading) {
                            Text("Halaman ${pagerState.currentPage + 1} dari ${bitmaps.size}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.stopAudio()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFF2B2B2B))
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF00D639))
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    pageSpacing = 16.dp
                ) { pageIndex ->
                    Card(
                        elevation = CardDefaults.cardElevation(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f).align(Alignment.Center)
                    ) {
                        Image(
                            bitmap = bitmaps[pageIndex].asImageBitmap(),
                            contentDescription = "Halaman ${pageIndex + 1}",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // ... (Audio Controller Overlay tetap sama)
        }
    }
}