package com.yatlunah.app.ui.screen.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatlunah.app.R

@Composable
fun AdminDashboardScreen(
    namaAdmin: String,
    onNavigateToUserMgmt: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    // brightGreen tetap dipertahankan sebagai warna identitas brand
    val brightGreen = Color(0xFF00D639)

    Scaffold(
        bottomBar = {
            BottomAppBar(
                // ✅ NAVBAR TETAP PUTIH: Di-hardcode ke Color.White sesuai permintaan
                containerColor = Color.White,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Home, null, tint = brightGreen, modifier = Modifier.size(28.dp))
                    }
                    IconButton(onClick = onNavigateToUserMgmt) {
                        // ✅ IKON TETAP ABU-ABU: Agar kontras di atas navbar putih
                        Icon(Icons.Default.List, null, tint = Color.Gray)
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person,  null, tint = Color.Gray)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // ✅ PERBAIKAN: Gunakan background dari tema
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    // ✅ PERBAIKAN: Gunakan onBackground untuk teks utama
                    Text("Assalamualaikum !,", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                    Text("Admin $namaAdmin", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
                }
                Image(
                    painter = painterResource(id = R.drawable.logo_yatlunah),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp))
                )
            }

            // --- 1. OVERVIEW STATUS SISTEM (Hijau) ---
            Card(
                colors = CardDefaults.cardColors(containerColor = brightGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Teks di sini tetap Color.White karena background-nya selalu brightGreen
                    Text("Overview Status Sistem", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Total User: 1,300 | Guru: 38 | Peserta: 1,262", color = Color.White.copy(0.9f), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- 2. LOG AKTIVITAS TERBARU ---
            // ✅ PERBAIKAN: Gunakan onBackground
            Text("Log Aktivitas Terbaru", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                // ✅ PERBAIKAN: Gunakan surface untuk Card
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    AdminLogActivityRow("Admin A", "Menambah Guru Baru", "1 jam lalu")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(0.3f))
                    AdminLogActivityRow("User B", "Melakukan Registrasi", "3 jam lalu")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- 3. MENU KOTAK ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminSmallMenuCard(Modifier.weight(1f), "Manajemen User", "Cek & Edit", Icons.Default.Group, onNavigateToUserMgmt)
                AdminSmallMenuCard(Modifier.weight(1f), "Manajemen Materi", "Update PDF", Icons.Default.LibraryBooks)
                AdminSmallMenuCard(Modifier.weight(1f), "Laporan", "Rekap Nilai", Icons.Default.Assessment)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- 4. QUICK ACTION SECTION ---
            // ✅ PERBAIKAN: Gunakan onBackground
            Text("Kontrol Manajemen", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                // ✅ PERBAIKAN: Gunakan surface untuk Card
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, null, tint = brightGreen)
                        Spacer(Modifier.width(8.dp))
                        // ✅ PERBAIKAN: Gunakan onSurface
                        Text("Akses Cepat Admin", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    }
                    Text("Kelola hak akses dan perizinan user dalam satu klik.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToUserMgmt,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = brightGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        // Teks tetap Color.White karena tombol selalu hijau
                        Text("Buka Manajemen User \uD83D\uDC65", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminLogActivityRow(nama: String, aksi: String, waktu: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(35.dp).background(Color.LightGray, CircleShape))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            // ✅ PERBAIKAN: Gunakan onSurface untuk nama
            Text(nama, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(aksi, fontSize = 12.sp, color = Color.Gray)
        }
        Text(waktu, fontSize = 10.sp, color = Color.LightGray)
    }
}

@Composable
fun AdminSmallMenuCard(modifier: Modifier, label: String, sub: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier.clickable { onClick() },
        // ✅ PERBAIKAN: Gunakan surface untuk Card
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Color(0xFFFFA000), modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            // ✅ PERBAIKAN: Gunakan onSurface untuk label
            Text(label, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center)
            Text(sub, fontSize = 9.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}