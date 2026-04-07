package com.yatlunah.app.ui.screen.guru

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruControlCenterScreen(
    idGuru: String,
    onNavigateToSetoran: (String) -> Unit,
    onNavigateToBimbingan: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pusat Kendali Guru") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Pilih Menu Operasional",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 1. Menu Setoran
            MenuCard(
                title = "Antrean Setoran Jilid",
                description = "Nilai bacaan dan setoran hafalan santri.",
                icon = Icons.Default.List,
                onClick = { onNavigateToSetoran(idGuru) }
            )

            // 2. Menu Bimbingan
            MenuCard(
                title = "Permintaan Bimbingan",
                description = "Terima atau tolak pendaftaran bimbingan baru.",
                icon = Icons.Default.CheckCircle,
                onClick = { onNavigateToBimbingan(idGuru) }
            )

            // 3. Menu Placeholder (Bisa ditambahkan nanti)
            MenuCard(
                title = "Jadwal & Laporan",
                description = "Lihat rekap aktivitas dan jadwal mengajar.",
                icon = Icons.Default.DateRange,
                onClick = { /* TODO: Fitur selanjutnya */ }
            )
            // Menu 3: Santri Aktif Saya (Tambahkan di bawah Menu Bimbingan)
            MenuCard(
                title = "Santri Bimbingan Saya",
                description = "Kelola dan hubungi santri yang sudah Anda terima.",
                icon = Icons.Default.Person, // Jangan lupa import Icons.Default.Person
                onClick = { /* Nanti kita arahkan dari MainActivity */ }
            )
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    }
}