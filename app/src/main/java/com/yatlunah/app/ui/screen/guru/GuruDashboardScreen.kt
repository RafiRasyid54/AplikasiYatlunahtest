package com.yatlunah.app.ui.screen.guru

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatlunah.app.R

@Composable
fun GuruDashboardScreen(
    namaGuru: String,
    onNavigateToAntrean: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val brightGreen = Color(0xFF00D639)
    val lightGrayBg = Color(0xFFF4F5F7)

    Scaffold(
        bottomBar = {
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
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Home, null, tint = brightGreen, modifier = Modifier.size(28.dp))
                    }
                    IconButton(onClick = onNavigateToAntrean) {
                        Icon(Icons.Default.LibraryBooks, null, tint = Color.Gray)
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, null, tint = Color.Gray)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(lightGrayBg)
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
                    Text("Assalamualaikum !,", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    Text("Ustadz $namaGuru", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                }
                Image(
                    painter = painterResource(id = R.drawable.logo_yatlunah),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp))
                )
            }

            // --- 1. OVERVIEW STATUS SISWA ---
            Card(
                colors = CardDefaults.cardColors(containerColor = brightGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Overview Status Siswa", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Total Siswa Aktif: 125 | Siswa Butuh Perhatian: 8", color = Color.White.copy(0.9f), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- 2. AKTIVITAS HARI INI ---
            Text("Aktivitas Hari Ini", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    AktivitasRow("Siswa A", "Menyelesaikan Jilid 2", "5 menit lalu")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Faded)
                    AktivitasRow("Siswa B", "Mendaftarkan Bimbingan", "20 menit lalu")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- 3. MENU KOTAK (Monitoring, Koreksi, Umpan Balik) ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SmallMenuCard(Modifier.weight(1f), "Monitoring", "95% Kehadiran", Icons.Default.BarChart)
                SmallMenuCard(Modifier.weight(1f), "Koreksi Hafalan", "12 Setoran", Icons.Default.FolderOpen, onNavigateToAntrean)
                SmallMenuCard(Modifier.weight(1f), "Umpan Balik", "3 Pesan Baru", Icons.Default.Chat)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- 4. VALIDASI SETORAN SECTION ---
            Text("Bimbingan & Validasi Setoran", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = brightGreen)
                        Spacer(Modifier.width(8.dp))
                        Text("Penyetoran Menunggu Validasi", fontWeight = FontWeight.Bold)
                    }
                    Text("Siswa A - Setoran Jilid 3 Hal 10", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                    Text("Siswa B - Setoran Jilid 1 Hal 5", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToAntrean,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = brightGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Validasi Setoran ✅", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AktivitasRow(nama: String, aksi: String, waktu: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(35.dp).background(Color.LightGray, CircleShape)) // Placeholder Foto
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(nama, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(aksi, fontSize = 12.sp, color = Color.Gray)
        }
        Text(waktu, fontSize = 10.sp, color = Color.LightGray)
    }
}

@Composable
fun SmallMenuCard(modifier: Modifier, label: String, sub: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Color(0xFFFFA000), modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(label, fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Text(sub, fontSize = 9.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

private val Color.Companion.Faded: Color get() = Color.LightGray.copy(alpha = 0.3f)