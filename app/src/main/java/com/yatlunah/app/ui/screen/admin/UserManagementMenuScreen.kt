package com.yatlunah.app.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementMenuScreen(
    onBack: () -> Unit,
    onNavigateToList: (String) -> Unit // Mengirim role yang dipilih (peserta/guru/admin)
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manajemen User", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF4F5F7)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Pilih Kategori User",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Menu untuk Peserta/Siswa
            UserTypeCard(
                title = "Daftar Peserta (Siswa)",
                count = "1.262 Orang",
                icon = Icons.Default.School,
                color = Color(0xFF28A745),
                onClick = { onNavigateToList("peserta") }
            )

            // Menu untuk Guru
            UserTypeCard(
                title = "Daftar Guru / Pengajar",
                count = "38 Orang",
                icon = Icons.Default.MenuBook,
                color = Color(0xFF1976D2),
                onClick = { onNavigateToList("guru") }
            )

            // Menu untuk Admin
            UserTypeCard(
                title = "Daftar Administrator",
                count = "2 Orang",
                icon = Icons.Default.AdminPanelSettings,
                color = Color(0xFFE64A19),
                onClick = { onNavigateToList("admin") }
            )
        }
    }
}

@Composable
fun UserTypeCard(
    title: String,
    count: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.padding(12.dp).size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(count, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}