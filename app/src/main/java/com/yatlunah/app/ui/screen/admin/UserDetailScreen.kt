package com.yatlunah.app.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.data.model.UserResponse
import com.yatlunah.app.data.remote.RetrofitClient
import com.yatlunah.app.ui.screen.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: String,    // Diambil dari kolom 'id' di database Anda
    userName: String,
    userEmail: String,
    initialRole: String,
    onBack: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    // 1. Token Warna Brand Yatlunah
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF0FDF4)
    val surfaceColor = if (isDark) Color(0xFF1E293B) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val brandGreen = Color(0xFF22C55E)

    // 2. State Management
    var selectedRole by remember { mutableStateOf(initialRole) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var tempRole by remember { mutableStateOf("") }

    var showGuruPicker by remember { mutableStateOf(false) }
    var guruList by remember { mutableStateOf<List<UserResponse>>(emptyList()) }
    var isLoadingGuru by remember { mutableStateOf(false) }

    val roleColor = when (selectedRole.lowercase()) {
        "guru" -> Color(0xFF3B82F6)
        "admin" -> Color(0xFFF59E0B)
        else -> brandGreen
    }

    // 3. Lifecycle Effects
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            profileViewModel.fetchUserStats(userId)
        }
    }

    LaunchedEffect(showGuruPicker) {
        if (showGuruPicker && guruList.isEmpty()) {
            isLoadingGuru = true
            try {
                // Mengambil daftar guru untuk plotting kelompok
                val response = RetrofitClient.authApi.getUsersByRole("guru", null)
                if (response.isSuccessful) {
                    guruList = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                android.util.Log.e("YATLUNAH_DEBUG", "Gagal load guru: ${e.message}")
            } finally {
                isLoadingGuru = false
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profil Pengguna", fontWeight = FontWeight.Black, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = bgColor)
            )
        },
        containerColor = bgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- HEADER: Avatar ---
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(8.dp, CircleShape)
                    .background(roleColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val initial = if (userName.isNotEmpty()) userName.take(1).uppercase() else "?"
                Text(initial, color = roleColor, fontSize = 40.sp, fontWeight = FontWeight.Black)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = userName.ifEmpty { "Tanpa Nama" }, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
                Text(text = userEmail.ifEmpty { "Email tidak tersedia" }, fontSize = 14.sp, color = Color.Gray)
            }

            // --- CARD 1: Manajemen Akses (Update Role) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SwapHoriz, null, tint = roleColor)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Ubah Peran", fontWeight = FontWeight.Bold, color = textColor)
                    }
                    Text(
                        "Ubah hak akses pengguna menjadi Guru atau Santri.",
                        fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp)
                    )

                    val targetRole = if (selectedRole.lowercase() == "santri") "guru" else "santri"
                    Button(
                        onClick = { tempRole = targetRole; showRoleDialog = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if(targetRole == "guru") Color(0xFF3B82F6) else brandGreen)
                    ) {
                        Text("Jadikan ${targetRole.uppercase()}", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // --- CARD 2: Plotting Kelompok (Sesuai Struktur Database: id_mitra) ---
            if (selectedRole.lowercase() == "santri") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GroupAdd, null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Kelompok Bimbingan", fontWeight = FontWeight.Bold, color = textColor)
                        }
                        Text(
                            "Tetapkan salah satu dari 8 ustadz pembimbing. Ini akan memperbarui kolom 'id_mitra' di database.",
                            fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Button(
                            onClick = { showGuruPicker = true },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) {
                            Text("Tetapkan Ustadz", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // --- DIALOG PICKER GURU (Fix NullPointerException) ---
    if (showGuruPicker) {
        AlertDialog(
            onDismissRequest = { showGuruPicker = false },
            containerColor = surfaceColor,
            title = { Text("Pilih Pembimbing", fontWeight = FontWeight.Bold) },
            text = {
                Box(modifier = Modifier.heightIn(max = 300.dp)) {
                    if (isLoadingGuru) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = brandGreen)
                    } else {
                        LazyColumn {
                            items(guruList) { guru ->
                                // SAFE CHECK: Memberikan fallback agar 'id' tidak null
                                val safeGuruId = guru.userId ?: ""
                                val safeNama = guru.nama_lengkap ?: "Tanpa Nama"

                                ListItem(
                                    headlineContent = { Text(safeNama, fontWeight = FontWeight.Medium) },
                                    modifier = Modifier.clickable {
                                        if (safeGuruId.isNotEmpty()) {
                                            // Memanggil fungsionalitas update id_mitra di backend
                                            profileViewModel.updateUserRole(userId, selectedRole) {
                                                showGuruPicker = false
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGuruPicker = false }) { Text("Batal", color = Color.Gray) }
            }
        )
    }

    // --- DIALOG KONFIRMASI ROLE ---
    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            containerColor = surfaceColor,
            title = { Text("Konfirmasi") },
            text = { Text("Yakin ingin mengubah peran $userName menjadi ${tempRole.uppercase()}?") },
            confirmButton = {
                Button(
                    onClick = {
                        profileViewModel.updateUserRole(userId, tempRole) {
                            selectedRole = tempRole
                            showRoleDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
                ) { Text("Ya, Ubah") }
            },
            dismissButton = {
                TextButton(onClick = { showRoleDialog = false }) { Text("Batal") }
            }
        )
    }
}