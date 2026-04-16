package com.yatlunah.app.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
    // Tema & Warna Dinamis
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF0F0F0F) else Color(0xFFF4F5F7)
    val surfaceColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)

    // State
    var selectedRole by remember { mutableStateOf(initialRole) }
    var showDialog by remember { mutableStateOf(false) }
    var tempRole by remember { mutableStateOf("") }

    // Warna Identitas Role
    val roleColor = when (selectedRole.lowercase()) {
        "guru" -> Color(0xFF3B82F6)   // Biru
        "admin" -> Color(0xFFF59E0B)  // Oranye
        else -> Color(0xFF00D639)     // Hijau Santri
    }

    LaunchedEffect(userId) {
        profileViewModel.fetchUserStats(userId)
    }

    val stats = profileViewModel.userStats.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Detail Pengguna", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bgColor,
                    titleContentColor = textColor,
                    navigationIconContentColor = textColor
                )
            )
        },
        containerColor = bgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp), // Sedikit diperlebar
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER: Avatar & Info Dasar ---
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .shadow(if (isDark) 0.dp else 8.dp, CircleShape, spotColor = roleColor.copy(alpha = 0.3f))
                    .background(roleColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1).uppercase(),
                    color = roleColor,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = userName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = userEmail, fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(16.dp))

            // Badge Role Saat Ini
            Surface(
                color = roleColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = selectedRole.uppercase(),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = roleColor,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // --- SEKSI PERUBAHAN ROLE ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = roleColor, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Manajemen Akses",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = textColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Pilih peran yang sesuai untuk menentukan hak akses pengguna ini di aplikasi.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Logic: Tampilkan tombol yang BUKAN role saat ini
                    val targetRole = if (selectedRole == "santri") "guru" else "santri"
                    val targetColor = if (targetRole == "guru") Color(0xFF3B82F6) else Color(0xFF00D639)

                    Button(
                        onClick = {
                            tempRole = targetRole
                            showDialog = true
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = targetColor)
                    ) {
                        Text(
                            text = "Ubah menjadi ${targetRole.replaceFirstChar { it.uppercase() }}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- STATISTIK BELAJAR ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Text(
                    text = "Aktivitas Belajar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Hari Beruntun", "${stats?.currentStreak ?: 0}", roleColor)

                    // Divider Vertikal Tipis
                    VerticalDivider(modifier = Modifier.height(40.dp), color = Color.Gray.copy(alpha = 0.2f))

                    StatItem("Jilid", "${stats?.lastJilid ?: 1}", roleColor)

                    VerticalDivider(modifier = Modifier.height(40.dp), color = Color.Gray.copy(alpha = 0.2f))

                    StatItem("Halaman", "${stats?.lastHalaman ?: 0}", roleColor)
                }
            }

            // --- PROGRESS TOTAL ---
            Spacer(modifier = Modifier.height(28.dp))

            val progressValue = stats?.totalProgress ?: 0f
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Penyelesaian Program", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textColor)
                Text("${(progressValue * 100).toInt()}%", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = roleColor)
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progressValue },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = roleColor,
                trackColor = roleColor.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(40.dp)) // Ruang napas di bawah
        }
    }

    // --- DIALOG KONFIRMASI ---
    if (showDialog) {
        val dialogBtnColor = if(tempRole == "guru") Color(0xFF3B82F6) else Color(0xFF00D639)

        AlertDialog(
            containerColor = surfaceColor,
            titleContentColor = textColor,
            textContentColor = Color.Gray,
            shape = RoundedCornerShape(20.dp), // Sudut dialog lebih membulat
            onDismissRequest = { showDialog = false },
            title = { Text("Konfirmasi Perubahan", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin mengubah peran $userName menjadi ${tempRole.uppercase()}? \n\nPengguna ini akan segera dialihkan ke dasbor ${if(tempRole == "guru") "pengajar" else "santri"}.",
                    lineHeight = 20.sp,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        profileViewModel.updateUserRole(userId, tempRole) {
                            selectedRole = tempRole
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = dialogBtnColor),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Ya, Ubah Role", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = color)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
    }
}