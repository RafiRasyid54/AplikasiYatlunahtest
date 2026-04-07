package com.yatlunah.app.ui.screen.santri

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List // ✅ Ganti yang deprecated
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SantriControlCenterScreen(
    userId: String,
    namaUser: String,
    emailUser: String,
    navController: NavController, // ✅ Pastikan ini ada di parameter
    onNavigateToMateri: () -> Unit,
    onNavigateToRiwayat: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val bgColor = Color(0xFFF4F5F7)
    val brightGreen = Color(0xFF00D639)

    Scaffold(
        bottomBar = {
            // NAVBAR BAWAH
            BottomAppBar(
                containerColor = Color.White,
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.Gray)
                    }
                    IconButton(onClick = { /* Stay here */ }) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Menu", tint = brightGreen)
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, "Profile", tint = Color.Gray)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Text(
                text = "Menu Santri",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Text(text = "Halo, $namaUser! Pilih menu di bawah.", color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            // DAFTAR MENU
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                // 1. Menu Belajar
                MenuCard(
                    title = "Materi dan Riwayat Setoran",
                    subtitle = "Baca materi dan setoran hafalan.",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    iconColor = brightGreen,
                    onClick = onNavigateToMateri
                )

                // 2. Menu Bimbingan (Diarahkan ke DETAIL, bukan langsung DAFTAR)
                MenuCard(
                    title = "Bimbingan Saya",
                    subtitle = "Cek status bimbingan & hubungi Guru.",
                    icon = Icons.Default.People,
                    iconColor = Color(0xFF00BCC9),
                    onClick = {
                        navController.navigate("santri_bimbingan_detail/$userId/$namaUser/$emailUser")
                    }
                )
            }
        }
    }
}

// ✅ PASTIKAN FUNGSI MENUCARD MEMILIKI SEMUA PARAMETER INI
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
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick // ✅ Menggunakan parameter onClick dari Card langsung
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconColor.copy(alpha = 0.1f)
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.padding(12.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}