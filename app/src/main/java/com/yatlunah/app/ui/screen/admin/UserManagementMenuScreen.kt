package com.yatlunah.app.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatlunah.app.data.remote.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementMenuScreen(
    onBack: () -> Unit,
    onNavigateToList: (String) -> Unit
) {
    // ✅ Dukungan Dark Mode Otomatis
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF0F0F0F) else Color(0xFFF4F5F7)
    val surfaceColor = if (isDark) Color(0xFF1A1A1A) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)

    // ✅ PERBAIKAN: Default key disamakan dengan pemanggilan (santri, bukan peserta)
    var counts by remember { mutableStateOf(mapOf("santri" to 0, "guru" to 0, "admin" to 0)) }
    var isLoading by remember { mutableStateOf(true) }

    // Mengambil data jumlah user dari database
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.authApi.getUsersCount()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    counts = body
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("YATLUNAH_DEBUG", "Gagal mengambil data user: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = { Text("Manajemen User", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor,
                    titleContentColor = textColor,
                    navigationIconContentColor = textColor
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Menampilkan loading dengan warna brand
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color(0xFF00D639), // Warna Hijau Yatlunah
                    trackColor = Color(0xFF00D639).copy(alpha = 0.2f)
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp)) // Menjaga tata letak agar tidak loncat setelah loading
            }

            UserTypeCard(
                title = "Daftar Peserta (Siswa)",
                count = "${counts["santri"] ?: 0} Orang",
                icon = Icons.Default.School,
                color = Color(0xFF00D639), // Hijau
                surfaceColor = surfaceColor,
                textColor = textColor,
                onClick = { onNavigateToList("santri") }
            )

            UserTypeCard(
                title = "Daftar Guru / Pengajar",
                count = "${counts["guru"] ?: 0} Orang",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                color = Color(0xFF3B82F6), // Biru
                surfaceColor = surfaceColor,
                textColor = textColor,
                onClick = { onNavigateToList("guru") }
            )

            UserTypeCard(
                title = "Daftar Administrator",
                count = "${counts["admin"] ?: 0} Orang",
                icon = Icons.Default.AdminPanelSettings,
                color = Color(0xFFF59E0B), // Oranye/Amber
                surfaceColor = surfaceColor,
                textColor = textColor,
                onClick = { onNavigateToList("admin") }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTypeCard(
    title: String,
    count: String,
    icon: ImageVector,
    color: Color,
    surfaceColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (surfaceColor == Color.White) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Latar belakang ikon dinamis
            Surface(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.padding(14.dp).size(28.dp))
            }

            Spacer(modifier = Modifier.width(18.dp))

            // Teks Title & Count
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text(count, fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            }

            // Ikon Panah Premium
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ChevronRight, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
        }
    }
}