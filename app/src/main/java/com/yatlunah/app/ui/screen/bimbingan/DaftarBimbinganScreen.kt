package com.yatlunah.app.ui.screen.bimbingan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Gunakan AutoMirrored
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarBimbinganScreen(
    userId: String,
    totalProgress: Int,
    onBack: () -> Unit,
    viewModel: BimbinganViewModel = viewModel(factory = BimbinganViewModel.Factory)
) {
    // Pindahkan definisi isReady ke sini agar bisa diakses oleh semua komponen di bawahnya
    val isReady = totalProgress >= 80

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pendaftaran Bimbingan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // Perbaikan: Gunakan Icons.AutoMirrored sesuai saran compiler
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Memberikan jarak antar elemen agar tidak "jelek"
        ) {
            Text(
                text = "Tingkatkan kualitas bacaan Anda dengan bimbingan langsung dari Guru Yatlunah.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            // Card Status Kelayakan [cite: 65, 73]
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Analisis Progres Belajar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Capaian Saat Ini: $totalProgress%", fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = (if (isReady) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isReady) "✓ Anda Layak Mengikuti Bimbingan" else "⚠ Selesaikan Materi Hingga 80%",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = if (isReady) Color(0xFF2E7D32) else Color(0xFFC62828),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Daftar Online [cite: 80]
            Button(
                onClick = { viewModel.submitPendaftaran(userId, "Online") },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                enabled = isReady && viewModel.uiState !is BimbinganUiState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D639))
            ) {
                Text("Daftar Bimbingan Online", fontWeight = FontWeight.Bold)
            }

            // Tombol Daftar Tatap Muka [cite: 82]
            OutlinedButton(
                onClick = { viewModel.submitPendaftaran(userId, "Tatap Muka") },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                enabled = isReady && viewModel.uiState !is BimbinganUiState.Loading
            ) {
                Text("Daftar Bimbingan Tatap Muka", fontWeight = FontWeight.Bold)
            }

            // Status Feedback
            when (val state = viewModel.uiState) {
                is BimbinganUiState.Loading -> CircularProgressIndicator(color = Color(0xFF00D639))
                is BimbinganUiState.Success -> {
                    Text(
                        "Pendaftaran Berhasil! Guru akan segera memproses antrean Anda.",
                        color = Color(0xFF2E7D32),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
                is BimbinganUiState.Error -> {
                    Text(state.message, color = Color.Red, textAlign = TextAlign.Center)
                }
                else -> {}
            }
        }
    }
}