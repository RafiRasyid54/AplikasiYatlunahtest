package com.yatlunah.app.ui.screen.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.yatlunah.app.R

@Composable
fun AdminDashboardScreen(
    onNavigateToUserMgmt: () -> Unit,
    // Tambahkan parameter navigasi lainnya jika perlu
    onNavigateToHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val darkGreen = Color(0xFF1B5E20)
    val softGreen = Color(0xFF28A745)

    Scaffold(
        // --- TAMBAHKAN BOTTOM BAR DI SINI ---
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Membuatnya melayang
                shape = RoundedCornerShape(30.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tombol Home
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = softGreen)
                    }
                    // Tombol Menu Book (Materi)
                    IconButton(onClick = { /* Navigasi Materi */ }) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.LightGray)
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, null, tint = Color.Gray)
                    }
                }
            }
        },
        containerColor = Color(0xFFF4F5F7) // Background seluruh layar
    ) { innerPadding ->
        // Bungkus Column lama kamu di dalam padding Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Menjaga konten tidak tertutup Navbar
                .verticalScroll(rememberScrollState())
        ) {
            // --- Header Section ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Assalamualaikum !,", fontSize = 16.sp, color = Color.Gray)
                    Text("Pak Rasyid", fontSize = 24.sp, fontWeight = FontWeight.Black)
                }
                Image(
                    painter = painterResource(id = R.drawable.logo_yatlunah),
                    contentDescription = null,
                    modifier = Modifier.size(45.dp)
                )
            }

            // --- Overview Status Card ---
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = softGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Overview Status Sistem", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Total Siswa Aktif: 125 | Total Guru Perhatian: 8", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatBox("Total User", "1,300", Modifier.weight(1f))
                        StatBox("Total Guru", "38", Modifier.weight(1f))
                        StatBox("Total Peserta", "1,262", Modifier.weight(1f))
                    }
                }
            }

            // --- Menu Action List ---
            Spacer(modifier = Modifier.height(24.dp))
            AdminMenuButton("Manajemen User", "Cari, edit role, aktifkan/nonaktifkan akun", Icons.Default.Group, onNavigateToUserMgmt)
            AdminMenuButton("Manajemen Materi", "Unggah dan perbarui konten bimbingan", Icons.Default.MenuBook) {}
            AdminMenuButton("Laporan", "Rekapitulasi nilai dan progres seluruh peserta", Icons.Default.Assessment) {}

            // Tambahkan spacer bawah agar konten terakhir tidak mepet navbar
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatBox(label: String, value: String, modifier: Modifier) {
    Surface(modifier = modifier, color = Color.White, shape = RoundedCornerShape(8.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(label, fontSize = 9.sp, color = Color.Gray)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminMenuButton(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(desc, fontSize = 10.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}