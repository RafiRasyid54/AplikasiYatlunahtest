package com.yatlunah.app.ui.screen.materi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

// ✅ Definisikan Model Data Lokal agar tidak "Unresolved Reference"
data class JilidData(
    val id: Int,
    val title: String,
    val status: String,
    val progress: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JilidListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToHome: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    // ✅ 1. Sediakan data list secara manual (atau ambil dari ViewModel nantinya)
    // Di JilidListScreen.kt
    val listJilid = remember {
        listOf(
            JilidData(1, "Iqra Jilid 1", "Tersedia", 1.0f),
            JilidData(2, "Iqra Jilid 2", "Tersedia", 0.5f),
            JilidData(3, "Iqra Jilid 3", "Tersedia", 0.0f)
            // Jilid 4, 5, 6 di-comment dulu atau dihapus
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Jilid Iqra", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF4F5F7)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listJilid) { jilid ->
                JilidCard(jilid = jilid, onClick = { onNavigateToDetail(jilid.id) })
            }
        }
    }
}

@Composable
fun JilidCard(jilid: JilidData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikon Jilid
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF00D639).copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("${jilid.id}", color = Color(0xFF00D639), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // ✅ Perbaikan: Menggunakan jilid.title, jilid.status sesuai model
                Text(jilid.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(jilid.status, color = Color.Gray, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar Kecil
                LinearProgressIndicator(
                    progress = { jilid.progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = Color(0xFF00D639),
                    trackColor = Color(0xFFEEEEEE)
                )
            }
        }
    }
}