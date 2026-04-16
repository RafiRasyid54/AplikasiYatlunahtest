package com.yatlunah.app.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GroupOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatlunah.app.data.model.UserResponse
import com.yatlunah.app.data.remote.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    role: String,
    onBack: () -> Unit,
    onNavigateToDetail: (String, String, String) -> Unit
) {
    // ✅ Dukungan Dark Mode
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF0F0F0F) else Color(0xFFF4F5F7)
    val surfaceColor = if (isDark) Color(0xFF1A1A1A) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)

    // ✅ Warna Aksen Sesuai Role
    val accentColor = when (role.lowercase()) {
        "guru" -> Color(0xFF3B82F6)   // Biru
        "admin" -> Color(0xFFF59E0B)  // Oranye
        else -> Color(0xFF00D639)     // Hijau Yatlunah (Santri)
    }

    var userList by remember { mutableStateOf<List<UserResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(role) {
        try {
            val response = RetrofitClient.authApi.getUsersByRole(role)
            if (response.isSuccessful) {
                userList = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("YATLUNAH_DEBUG", "Gagal ambil list: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Daftar ${role.replaceFirstChar { it.uppercase() }}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(bgColor)
        ) {
            if (isLoading) {
                // Loading State
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = accentColor
                )
            } else if (userList.isEmpty()) {
                // ✅ Empty State (Jika data tidak ada)
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.GroupOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Belum ada data ${role.lowercase()}.",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Data List
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(userList) { user ->
                        UserItem(
                            user = user,
                            accentColor = accentColor,
                            surfaceColor = surfaceColor,
                            textColor = textColor,
                            isDark = isDark
                        ) {
                            onNavigateToDetail(user.userId, user.nama_lengkap, user.email)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserItem(
    user: UserResponse,
    accentColor: Color,
    surfaceColor: Color,
    textColor: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick, // ✅ Menggunakan klik bawaan Card agar ripple effect-nya rapi
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar inisial nama
            Surface(
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = user.nama_lengkap.take(1).uppercase(),
                        color = accentColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info Teks
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.nama_lengkap,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = user.email,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            // Ikon penunjuk klik (Chevron)
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Detail",
                tint = Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}