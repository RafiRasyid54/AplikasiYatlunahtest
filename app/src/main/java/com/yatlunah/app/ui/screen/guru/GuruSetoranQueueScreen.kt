package com.yatlunah.app.ui.screen.guru

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.data.model.Setoran
import com.yatlunah.app.ui.viewmodel.GuruViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruSetoranQueueScreen(
    jilidTarget: Int,
    onBack: () -> Unit,
    onNavigateToPenilaian: (Setoran) -> Unit,
    viewModel: GuruViewModel = viewModel()
) {
    val semuaSetoran by viewModel.antreanSetoran.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Trigger ambil data dari API
    LaunchedEffect(jilidTarget) {
        viewModel.fetchAntrean(jilidTarget)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Antrean Jilid $jilidTarget", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F5F7)).padding(innerPadding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF00D639))
            } else if (semuaSetoran.isEmpty()) {
                Text(
                    "Tidak ada antrean untuk Jilid $jilidTarget",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(semuaSetoran) { itemSetoran ->
                        SetoranAntreanItem(
                            setoran = itemSetoran,
                            onClick = { onNavigateToPenilaian(itemSetoran) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SetoranAntreanItem(setoran: Setoran, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = Color(0xFF00D639).copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = (setoran.namaSantri ?: "S").take(1),
                        fontWeight = FontWeight.Bold, color = Color(0xFF00D639), fontSize = 20.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = setoran.namaSantri ?: "Siswa", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Jilid ${setoran.jilid} - Hal ${setoran.halaman}", fontSize = 13.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}