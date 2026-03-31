package com.yatlunah.app.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminControlCenterScreen(
    onNavigateToUserMgmt: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToLaporan: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pusat Kendali Admin", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        containerColor = Color(0xFFF4F5F7)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Manajemen Sistem & Konten",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // 1. Tombol ke User Management (Ganti UserManagementMenuScreen)
            AdminHubCard(
                title = "Manajemen Pengguna",
                desc = "Kelola Siswa & Guru, ubah role atau pantau status.",
                icon = Icons.Default.People,
                iconColor = Color(0xFF2196F3),
                onClick = onNavigateToUserMgmt
            )

            // 2. Tombol ke AdminQuoteScreen
            AdminHubCard(
                title = "Kutipan Harian (Quotes)",
                desc = "Tambah, hapus, atau atur quotes inspiratif harian.",
                icon = Icons.Default.FormatQuote,
                iconColor = Color(0xFF4CAF50),
                onClick = onNavigateToQuotes
            )

            // 3. Tombol ke Laporan (Screen Laporan kedepannya)
            AdminHubCard(
                title = "Laporan Aktivitas",
                desc = "Lihat statistik setoran dan grafik progres santri.",
                icon = Icons.Default.Assessment,
                iconColor = Color(0xFFFF9800),
                onClick = onNavigateToLaporan
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Informasi: Perubahan role pengguna akan langsung berdampak pada hak akses aplikasi mereka.",
                fontSize = 11.sp,
                color = Color.Gray,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun AdminHubCard(
    title: String,
    desc: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(26.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text(text = desc, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
            }
        }
    }
}