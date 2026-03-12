package com.yatlunah.app.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.yatlunah.app.data.model.UserResponse
import com.yatlunah.app.data.remote.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    role: String,
    onBack: () -> Unit,
    onNavigateToDetail: (String, String, String) -> Unit
) {
    var userList by remember { mutableStateOf<List<UserResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(role) {
        try {
            val response = RetrofitClient.authApi.getUsersByRole(role)
            if (response.isSuccessful) {
                userList = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("RAFI_DEBUG", "Gagal ambil list: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar ${role.replaceFirstChar { it.uppercase() }}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF4F5F7))) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF28A745))
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(userList) { user ->
                        UserItem(user) { onNavigateToDetail(user.userId, user.nama_lengkap, user.email) }
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(user: UserResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color(0xFF28A745).copy(0.1f)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(user.nama_lengkap.take(1).uppercase(), color = Color(0xFF28A745), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(user.nama_lengkap, fontWeight = FontWeight.Bold)
                Text(user.email, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}