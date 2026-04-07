package com.yatlunah.app.ui.screen.guru

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
// ✅ TAMBAHAN IMPORT UNTUK IKON BACK
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.data.model.Bimbingan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruBimbinganScreen(
    idGuru: String, // ✅ Tambahkan parameter ini
    onBack: () -> Unit,
    viewModel: GuruBimbinganViewModel = viewModel()
) {
    val antreanList by viewModel.antreanList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Load data saat pertama kali layar dibuka
    LaunchedEffect(Unit) {
        viewModel.fetchAntrean()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Permintaan Bimbingan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        // ... (Bagian loading & error tetap sama) ...

        if (antreanList.isEmpty() && !isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada santri yang mendaftar.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(antreanList) { bimbingan ->
                    ItemAntreanGuru(
                        bimbingan = bimbingan,
                        onTolak = { viewModel.updateStatus(bimbingan.id, "Ditolak", idGuru) },
                        onTerima = {
                            // ✅ Gunakan idGuru yang dilempar dari parameter fungsi
                            viewModel.updateStatus(bimbingan.id, "Diterima", idGuru)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemAntreanGuru(
    bimbingan: Bimbingan,
    onTolak: () -> Unit,
    onTerima: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ID Santri: ${bimbingan.userId}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Program: ${bimbingan.jenisBimbingan}", color = Color.Gray)
            Text("Tanggal: ${bimbingan.tanggalDaftar}", color = Color.Gray, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = onTolak,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.padding(end = 8.dp)
                ) { Text("Tolak") }

                Button(
                    onClick = onTerima,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D639))
                ) { Text("Terima") }
            }
        }
    }
}