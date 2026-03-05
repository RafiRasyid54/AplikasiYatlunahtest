package com.yatlunah.app.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.ui.screen.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: String,
    userName: String,
    userEmail: String,
    onBack: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    // ✅ Panggil data statistik dari database saat screen dibuka
    LaunchedEffect(userId) {
        profileViewModel.fetchUserStats(userId)
    }

    val stats = profileViewModel.userStats.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Progres Siswa", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        containerColor = Color(0xFFF4F5F7)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Header Section ---
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFF28A745), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1).uppercase(),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = userName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = userEmail, fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            // --- Statistik Section ---
            Text(
                text = "Ringkasan Belajar",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // ✅ Sesuaikan dengan UserStats.kt (currentStreak, lastJilid, lastHalaman)
                    StatItem("Streak", "${stats?.currentStreak ?: 0} Hari")
                    StatItem("Jilid", "${stats?.lastJilid ?: 1}")
                    StatItem("Halaman", "${stats?.lastHalaman ?: 0}")
                }
            }

            // --- Progress Bar Section ---
            Spacer(modifier = Modifier.height(24.dp))
            val progressValue = stats?.totalProgress ?: 0f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Progress Total", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("${(progressValue * 100).toInt()}% Selesai", fontSize = 12.sp, color = Color.Gray)
            }

            // ✅ Fix Deprecated: Gunakan lambda untuk progress
            LinearProgressIndicator(
                progress = { progressValue },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(8.dp),
                color = Color(0xFF28A745),
                trackColor = Color(0xFFE8F5E9)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Catatan Tambahan (Opsional) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9).copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Gunakan data ini untuk memantau konsistensi belajar siswa secara rutin.",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF28A745))
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}