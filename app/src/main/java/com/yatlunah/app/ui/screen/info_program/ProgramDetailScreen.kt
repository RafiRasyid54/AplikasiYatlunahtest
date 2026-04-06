package com.yatlunah.app.ui.screen.info_program

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yatlunah.app.data.model.ProgramYatlunah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramDetailScreen(
    program: ProgramYatlunah?,
    onBack: () -> Unit,
    onRegister: (Int) -> Unit,
    // Pastikan parameter ini ada
    fiturUnggulan: List<String> = emptyList()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(program?.nama ?: "Detail Program") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        if (program == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Data program tidak ditemukan")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Deskripsi", style = MaterialTheme.typography.titleMedium)
                Text(program.deskripsi, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))

                Text("Target Peserta", style = MaterialTheme.typography.titleMedium)
                Text(program.targetPeserta, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Text("Materi Utama", style = MaterialTheme.typography.titleMedium)
                Text(program.materiUtama, style = MaterialTheme.typography.bodyMedium)

                // --- BAGIAN BARU UNTUK MENAMPILKAN FITUR UNGGULAN ---
                if (fiturUnggulan.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Fitur Unggulan", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))

                    // Melakukan perulangan untuk mencetak setiap fitur
                    fiturUnggulan.forEach { fitur ->
                        Text(
                            text = fitur,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
                // ---------------------------------------------------

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onRegister(program.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Daftar Program Ini")
                }
            }
        }
    }
}