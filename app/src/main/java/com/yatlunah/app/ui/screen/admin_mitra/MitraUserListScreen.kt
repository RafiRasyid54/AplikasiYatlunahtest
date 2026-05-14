package com.yatlunah.app.ui.screen.admin_mitra

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.data.model.UserResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitraUserListScreen(
    role: String,
    idMitra: String,
    viewModel: MitraViewModel = viewModel(),
    onBack: () -> Unit,
    onNavigateToDetail: (String?, String?, String?) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF0F0F0F) else Color(0xFFF4F5F7)
    val surfaceColor = if (isDark) Color(0xFF1A1A1A) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)

    val accentColor = when (role.lowercase()) {
        "guru" -> Color(0xFF3B82F6)
        else -> Color(0xFF00D639)
    }

    val userList = viewModel.userList
    val isLoading = viewModel.isLoading

    LaunchedEffect(role, idMitra) {
        viewModel.fetchUsersByMitra(role, idMitra)
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text("Daftar ${role.replaceFirstChar { it.uppercase() }} Lembaga",
                        fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor, titleContentColor = textColor, navigationIconContentColor = textColor
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(bgColor)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = accentColor)
            } else if (userList.isEmpty()) {
                EmptyStateMitra(role = role)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(userList) { user ->
                        MitraUserItem(user, accentColor, surfaceColor, textColor, isDark) {
                            // PERBAIKAN: Menggunakan user.userId
                            onNavigateToDetail(user.userId, user.nama_lengkap, user.email)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateMitra(role: String) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.GroupOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Belum ada $role di lembaga Anda.", color = Color.Gray, fontSize = 16.sp, textAlign = TextAlign.Center)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitraUserItem(user: UserResponse, accentColor: Color, surfaceColor: Color, textColor: Color, isDark: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(46.dp), shape = CircleShape, color = accentColor.copy(alpha = 0.15f)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(user.nama_lengkap.take(1).uppercase(), color = accentColor, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.nama_lengkap, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                Text(user.email, fontSize = 13.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray.copy(alpha = 0.5f))
        }
    }
}