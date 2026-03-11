package com.yatlunah.app.ui.screen.materi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
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
// Pastikan import R ini sesuai dengan nama package aplikasimu
import com.yatlunah.app.R

@Composable
fun MenuBelajarScreen(
    namaUser: String,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMateri: () -> Unit,
    onNavigateToRiwayat: () -> Unit
) {
    val brightGreen = Color(0xFF00D639)

    Scaffold(
        bottomBar = {
            // ✅ NAVBAR DISEJAJARKAN 100% DENGAN DASHBOARD
            BottomAppBar(
                containerColor = Color.White,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tombol Kiri (Home) -> Pindah ke Dashboard
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.Gray)
                    }

                    // Tombol Tengah (List/Belajar) -> Aktif menyala hijau
                    IconButton(onClick = { /* Sudah di halaman ini */ }) {
                        Icon(
                            Icons.Default.List,
                            contentDescription = "Menu Belajar",
                            tint = brightGreen,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // Tombol Kanan (Profil) -> Pindah ke Profil
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.Gray)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F5F7))
                .padding(innerPadding)
        ) {

            // LOGO YATLUNAH
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pastikan R.drawable.logo_yatlunah sudah ada di foldermu (sama seperti di Dashboard)
                Image(
                    painter = painterResource(id = R.drawable.logo_yatlunah),
                    contentDescription = "Logo Yatlunah",
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Yatlunah",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF8DC63F)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // TEKS GREETING
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Pusat Pembelajaran",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ahlan wa sahlan, $namaUser!\nPilih menu di bawah untuk melanjutkan.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }

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