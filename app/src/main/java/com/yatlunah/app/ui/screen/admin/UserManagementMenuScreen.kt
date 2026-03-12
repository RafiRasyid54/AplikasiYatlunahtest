package com.yatlunah.app.ui.screen.admin

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
import androidx.compose.ui.graphics.Color
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
    var counts by remember { mutableStateOf(mapOf("peserta" to 0, "guru" to 0, "admin" to 0)) }
    var isLoading by remember { mutableStateOf(true) }

    // Mengambil data jumlah user dari database
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.authApi.getUsersCount()
            if (response.isSuccessful) {
                val body = response.body()
                android.util.Log.d("RAFI_DEBUG", "JSON API: $body")
                if (body != null) {
                    counts = body
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("RAFI_DEBUG", "Gagal: ${e.message}")
        } finally {
            // ✅ INI WAJIB ADA agar UI berhenti loading dan menampilkan angka
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manajemen User", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFF28A745))

            UserTypeCard(
                title = "Daftar Peserta (Siswa)",
                count = "${counts["peserta"] ?: 0} Orang",
                icon = Icons.Default.School,
                color = Color(0xFF28A745),
                onClick = { onNavigateToList("peserta") }
            )

            UserTypeCard(
                title = "Daftar Guru / Pengajar",
                count = "${counts["guru"] ?: 0} Orang",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                color = Color(0xFF1976D2),
                onClick = { onNavigateToList("guru") }
            )

            UserTypeCard(
                title = "Daftar Administrator",
                count = "${counts["admin"] ?: 0} Orang",
                icon = Icons.Default.AdminPanelSettings,
                color = Color(0xFFE64A19),
                onClick = { onNavigateToList("admin") }
            )
        }
    }
}

@Composable
fun UserTypeCard(title: String, count: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                Icon(icon, null, tint = color, modifier = Modifier.padding(12.dp).size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(count, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}