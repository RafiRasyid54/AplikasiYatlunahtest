package com.yatlunah.app.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    initialRole: String,
    onBack: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    // State untuk memantau role saat ini secara lokal
    var selectedRole by remember { mutableStateOf(initialRole) }
    var showDialog by remember { mutableStateOf(false) }
    var tempRole by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        profileViewModel.fetchUserStats(userId)
    }

    val stats = profileViewModel.userStats.value
    val brightGreen = Color(0xFF28A745)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail & Akses Pengguna", fontWeight = FontWeight.Bold) },
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Header Section ---
            Box(
                modifier = Modifier.size(80.dp).background(brightGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(userName.take(1).uppercase(), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = userName, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            // Badge Role Saat Ini
            Surface(
                color = if (selectedRole == "guru") Color(0xFFE3F2FD) else Color(0xFFE8F5E9),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = selectedRole.uppercase(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedRole == "guru") Color(0xFF1976D2) else brightGreen
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SEKSI PERUBAHAN ROLE (FOKUS JADI GURU) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Manajemen Peran Pengguna", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Ubah status pengguna menjadi Siswa atau Guru", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Tombol Set Jadi Siswa (User)
                        OutlinedButton(
                            onClick = {
                                tempRole = "user"
                                showDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            enabled = selectedRole != "user",
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = brightGreen)
                        ) {
                            Text("Jadi Siswa")
                        }

                        // ✅ PERBAIKAN: Tombol Set Jadi Guru
                        Button(
                            onClick = {
                                tempRole = "guru"
                                showDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            enabled = selectedRole != "guru",
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)) // Warna Biru untuk Guru
                        ) {
                            Text("Jadi Guru")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Statistik Section (Ringkasan Belajar) ---
            Text("Ringkasan Belajar", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatItem("Streak", "${stats?.currentStreak ?: 0} Hari")
                    StatItem("Jilid", "${stats?.lastJilid ?: 1}")
                    StatItem("Halaman", "${stats?.lastHalaman ?: 0}")
                }
            }

            // --- Progress Section ---
            Spacer(modifier = Modifier.height(24.dp))
            val progressValue = stats?.totalProgress ?: 0f
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Progress Total", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("${(progressValue * 100).toInt()}% Selesai", fontSize = 12.sp, color = Color.Gray)
            }
            LinearProgressIndicator(
                progress = { progressValue },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).height(8.dp),
                color = brightGreen,
                trackColor = Color(0xFFE8F5E9)
            )
        }
    }

    // --- DIALOG KONFIRMASI ---
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Konfirmasi Hak Akses") },
            text = { Text("Ubah peran $userName menjadi ${tempRole.uppercase()}? Pengguna ini akan mendapatkan akses fitur ${if(tempRole == "guru") "Penilaian" else "Belajar"}.") },
            confirmButton = {
                TextButton(onClick = {
                    profileViewModel.updateUserRole(userId, tempRole) {
                        selectedRole = tempRole
                        showDialog = false
                    }
                }) {
                    Text("Ya, Ubah", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF28A745))
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}