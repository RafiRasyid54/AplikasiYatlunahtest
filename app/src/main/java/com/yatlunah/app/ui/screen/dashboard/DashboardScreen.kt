package com.yatlunah.app.ui.screen.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.yatlunah.app.R
import java.util.*

@Composable
fun DashboardScreen(
    userId: String,
    namaUser: String,
    onLogout: () -> Unit,
    onNavigateToJilid: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val brightGreen = Color(0xFF00D639)
    val lightGrayBg = Color(0xFFF4F5F7)

    LaunchedEffect(userId) {
        viewModel.fetchStats(userId)
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
                modifier = Modifier.clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) {
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
                colors = CardDefaults.cardColors(containerColor = brightGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Quotes Hari Ini:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "\"Sesungguhnya orang-orang yang bersabar akan diberi pahala tanpa batas.\"",
                        color = Color.White, fontSize = 14.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Text("(QS. Az-Zumar: 10)", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, modifier = Modifier.align(Alignment.End))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                        progress = { viewModel.progressPercent },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = brightGreen,
                        trackColor = Color(0xFFF0F0F0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- 3. STATISTIK ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(modifier = Modifier.weight(1f), label = viewModel.streak, subLabel = "Streak", icon = "🔥")
                StatCard(modifier = Modifier.weight(1f), label = viewModel.lastRead, subLabel = viewModel.lastPage, icon = "📖")
                StatCard(modifier = Modifier.weight(1f), label = "Target", subLabel = "2/5 Hal", icon = "🏅")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 4. LANJUTKAN MEMBACA ---
            Text("Lanjutkan Aktivitas", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(60.dp), shape = RoundedCornerShape(12.dp), color = brightGreen.copy(alpha = 0.1f)) {
                        Icon(painter = painterResource(id = R.drawable.logo_yatlunah), null, tint = brightGreen, modifier = Modifier.padding(12.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(viewModel.lastRead, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(viewModel.lastPage, color = Color.Gray, fontSize = 13.sp)
                    }
                    Button(
                        onClick = onNavigateToJilid,
                        colors = ButtonDefaults.buttonColors(containerColor = brightGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                        Text("Mulai", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 5. BIMBINGAN ONLINE (YANG TADI HILANG) ---
            Text("Bimbingan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Bimbingan Online Terdekat", fontWeight = FontWeight.Bold)
                    Text("Besok, 16:00 WIB", color = Color.Gray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* Link Zoom */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCC9)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Masuk Kelas Online", fontSize = 14.sp)
                    }
                }
            }
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