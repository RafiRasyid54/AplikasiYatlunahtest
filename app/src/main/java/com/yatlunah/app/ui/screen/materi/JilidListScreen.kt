package com.yatlunah.app.ui.screen.materi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// ✅ Pastikan kedua import ini ada
import com.yatlunah.app.data.model.JilidData
import com.yatlunah.app.ui.screen.materi.JilidViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JilidListScreen(
    viewModel: JilidViewModel = viewModel(),
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToHome: () -> Unit
) {
    val listJilid by viewModel.jilidList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Jilid Iqra", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF4F5F7)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listJilid) { jilid ->
                JilidCard(
                    jilid = jilid,
                    onClick = {
                        // ✅ KLIK KARTU: Selalu arahkan ke halaman baca PDF (baik online maupun offline)
                        onNavigateToDetail(jilid.nomorJilid)
                    },
                    onDownloadClick = {
                        // ✅ KLIK IKON DOWNLOAD: Unduh file jika belum ada di HP
                        if (!jilid.isDownloaded) {
                            viewModel.downloadJilid(jilid)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun JilidCard(
    jilid: JilidData,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit // ✅ Tambahkan parameter khusus untuk tombol download
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Kartu diklik untuk BUKA materi
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF00D639).copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("${jilid.nomorJilid}", color = Color(0xFF00D639), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(jilid.judulJilid, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Ukuran: ${jilid.fileSize}", color = Color.Gray, fontSize = 12.sp)

                // Progress Bar saat sedang mendownload
                if (jilid.downloadProgress > 0f && jilid.downloadProgress < 1f) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { jilid.downloadProgress },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = Color(0xFF00D639),
                        trackColor = Color(0xFFEEEEEE)
                    )
                }
            }

            // ✅ Ikon sekarang menjadi IconButton agar bisa diklik secara terpisah dari kartunya
            IconButton(onClick = onDownloadClick) {
                Icon(
                    imageVector = if (jilid.isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                    contentDescription = if (jilid.isDownloaded) "Sudah Diunduh" else "Unduh Jilid",
                    tint = if (jilid.isDownloaded) Color(0xFF00D639) else Color.LightGray
                )
            }
        }
    }
}