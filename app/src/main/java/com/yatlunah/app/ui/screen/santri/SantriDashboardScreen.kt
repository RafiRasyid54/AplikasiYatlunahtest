package com.yatlunah.app.ui.screen.santri

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.yatlunah.app.R
import java.util.*

@Composable
fun SantriDashboardScreen(
    userId: String,
    namaUser: String,
    emailUser: String,
    navController: NavController,
    onLogout: () -> Unit,
    onNavigateToJilid: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToBimbingan: () -> Unit,
    onNavigateToInfoProgram: () -> Unit,
    viewModel: SantriViewModel = viewModel()
) {
    val brightGreen = Color(0xFF00D639)
    val lightGrayBg = Color(0xFFF4F5F7)

    LaunchedEffect(userId) {
        kotlinx.coroutines.delay(500)
        if (userId.isNotEmpty()) {
            viewModel.fetchStats(userId)
            viewModel.fetchStatusBimbingan(userId)
            viewModel.startQuoteTimer()
        }
    }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Selamat Pagi"
            in 12..14 -> "Selamat Siang"
            in 15..17 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                // Menggunakan clip agar sudut atas melengkung
                modifier = Modifier.clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateToDashboard) {
                        Icon(Icons.Default.Home, null, tint = brightGreen, modifier = Modifier.size(30.dp))
                    }
                    IconButton(onClick = onNavigateToJilid) {
                        Icon(Icons.Default.List, null, tint = Color.Gray)
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
                // ✅ PENTING: innerPadding digunakan di sini agar konten tidak tertutup BottomBar
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("$greeting!", fontSize = 18.sp, color = Color.Gray)
                    Text(namaUser, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Image(
                    painter = painterResource(id = R.drawable.logo_yatlunah),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp))
                )
            }

            // --- 1. CARD QUOTES ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF00D639)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Quotes Hari Ini:", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"${viewModel.currentQuote}\"",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // --- 2. CARD PROGRESS ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Progress :", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${(viewModel.progressPercent * 100).toInt()}%", color = brightGreen, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        // progress = { viewModel.progressPercent }, // Gunakan ini jika menggunakan Material3 terbaru
                        progress = viewModel.progressPercent,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = brightGreen,
                        trackColor = Color(0xFFF0F0F0)
                    )
                }
            }

            // --- 3. STATISTIK ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(modifier = Modifier.weight(1f), label = viewModel.streak, subLabel = "Streak", icon = "🔥")
                StatCard(modifier = Modifier.weight(1f), label = viewModel.lastRead, subLabel = viewModel.lastPage, icon = "📖")
                StatCard(modifier = Modifier.weight(1f), label = "Target", subLabel = "2/5 Hal", icon = "🏅")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Lanjutkan Belajar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToJilid() }, // Mengarah ke Control Center / Daftar Jilid
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(45.dp).background(brightGreen.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = brightGreen)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Materi Jilid ${viewModel.lastRead}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Lanjutkan membaca halaman ${viewModel.lastPage}", color = Color.Gray, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 4. BIMBINGAN ---
            Text("Bimbingan", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            // Kartu Status Bimbingan (Bisa diklik ke detail)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("santri_bimbingan_detail/$userId/$namaUser/$emailUser")
                    },
                // Transparent agar warna asli KartuStatusBimbingan yang muncul
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                KartuStatusBimbingan(
                    status = viewModel.bimbinganStatus,
                    namaGuruPembimbing = viewModel.namaGuru
                )
            }

            // Tampilkan tombol daftar jika status pendaftaran belum aktif
            val statusLow = viewModel.bimbinganStatus.lowercase()
            if (statusLow != "aktif" && statusLow != "diterima") {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Daftar Bimbingan Guru", fontWeight = FontWeight.Bold)
                        Text("Klik untuk mendaftar bimbingan online/offline", color = Color.Gray, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToBimbingan,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCC9)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Daftar Sekarang", fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- 5. INFORMASI ---
            Text("Informasi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Program Yatlunah", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Lihat daftar program belajar yang tersedia", color = Color.Gray, fontSize = 13.sp)
                    }
                    Button(
                        onClick = onNavigateToInfoProgram,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Lihat", fontSize = 12.sp)
                    }
                }
            }

            // ✅ Tambahkan Spacer di paling bawah agar card terakhir tidak terpotong navbar
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun KartuStatusBimbingan(status: String, namaGuruPembimbing: String) {
    val (warnaKartu, warnaTeks, pesan) = when (status.lowercase()) {
        "aktif", "diterima" -> Triple(
            Color(0xFFE8F5E9), Color(0xFF2E7D32),
            "Alhamdulillah! Bimbinganmu diterima oleh Ustadz/Ustadzah $namaGuruPembimbing."
        )
        "ditolak" -> Triple(
            Color(0xFFFFEBEE), Color(0xFFC62828),
            "Maaf, kuota bimbingan penuh. Silakan daftar kembali nanti."
        )
        else -> Triple(
            Color(0xFFFFF3E0), Color(0xFFEF6C00),
            "Menunggu konfirmasi penerimaan dari Guru."
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = warnaKartu),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Info Bimbingan", fontWeight = FontWeight.Bold, color = warnaTeks)
                Surface(
                    color = warnaTeks.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = status.uppercase(),
                        color = warnaTeks,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = pesan, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, label: String, subLabel: String, icon: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
            Text(subLabel, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}