package com.yatlunah.app.ui.screen.materi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatlunah.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuBelajarScreen(
    namaUser: String,
    onBack: () -> Unit, // ✅ Menggantikan parameter navigasi navbar sebelumnya
    onNavigateToMateri: () -> Unit,
    onNavigateToRiwayat: () -> Unit
) {
    val brightGreen = Color(0xFF00D639)
    val bgColor = Color(0xFFF4F5F7)

    Scaffold(
        // ✅ TAMBAHKAN TOMBOL BACK DI SINI
        topBar = {
            TopAppBar(
                title = { Text("Menu Belajar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor // Warna menyatu dengan background
                )
            )
        }
        // Navbar (bottomBar) sudah dihapus sepenuhnya
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(innerPadding)
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            // BUNGKUSAN KARTU MENU
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuCard(
                    title = "Materi Iqra",
                    subtitle = "Baca PDF, dengarkan audio, dan rekam setoranmu di sini.",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    iconColor = brightGreen,
                    onClick = onNavigateToMateri
                )

                MenuCard(
                    title = "Riwayat Setoran",
                    subtitle = "Lihat status penilaian dan catatan perbaikan dari Ustadz.",
                    icon = Icons.Default.AssignmentTurnedIn,
                    iconColor = Color(0xFF007BFF),
                    onClick = onNavigateToRiwayat
                )
            }
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
            }

            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Masuk", tint = Color.LightGray)
        }
    }
}