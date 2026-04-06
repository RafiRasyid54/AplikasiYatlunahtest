package com.yatlunah.app.ui.screen.info_program

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.data.model.ProgramYatlunah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramListScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: ProgramViewModel = viewModel()
) {
    val programs by viewModel.programs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Informasi Program Yatlunah") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(programs) { program ->
                // Teruskan fungsi navigasi dan ID program ke dalam Card
                ProgramCard(
                    program = program,
                    onClickDetail = { onNavigateToDetail(program.id.toString()) }
                )
            }
        }
    }
    // TOMBOL RAKSASA YANG SEBELUMNYA ADA DI SINI SUDAH DIHAPUS 🧹
}

@Composable
fun ProgramCard(
    program: ProgramYatlunah,
    onClickDetail: () -> Unit // Tambahkan parameter aksi klik di sini
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = program.nama,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = program.deskripsi,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Panggil fungsi navigasinya di tombol ini
            Button(onClick = onClickDetail) {
                Text("Lihat Detail & Daftar")
            }
        }
    }
}