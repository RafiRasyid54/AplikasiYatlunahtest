package com.yatlunah.app.ui.screen.materi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatlunah.app.data.model.Setoran // ✅ Pastikan path model Setoran benar
import com.yatlunah.app.data.remote.RetrofitClient // ✅ Pastikan path RetrofitClient benar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatSetoranScreen(
    userId: String,
    onBack: () -> Unit
) {
    var selectedJilid by remember { mutableStateOf<Int?>(null) }
    var listRiwayat by remember { mutableStateOf<List<Setoran>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Mengambil data dari Backend
    LaunchedEffect(userId) {
        try {
            val response = RetrofitClient.materiApi.getRiwayatSetoran(userId)
            if (response.isSuccessful) {
                listRiwayat = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            // Error handling bisa ditambahkan Toast di sini
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedJilid == null) "Riwayat Setoran" else "Jilid $selectedJilid",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedJilid != null) selectedJilid = null else onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F5F7))
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (selectedJilid == null) {
                // --- DAFTAR JILID ---
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items((1..6).toList()) { nomor ->
                        FolderJilidCard(nomor = nomor) {
                            selectedJilid = nomor
                        }
                    }
                }
            } else {
                // --- DAFTAR SETORAN DI JILID TERSEBUT ---
                val filteredList = listRiwayat.filter { it.jilid == selectedJilid }

                if (filteredList.isEmpty()) {
                    Text(
                        "Belum ada setoran di Jilid $selectedJilid",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredList) { setoran ->
                            RiwayatCard(setoran)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FolderJilidCard(nomor: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color(0xFF00D639).copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = Color(0xFF00D639),
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Riwayat Jilid $nomor",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}

@Composable
fun RiwayatCard(setoran: Setoran) {
    val isDinilai = setoran.status == "dinilai"
    val statusColor = if (isDinilai) Color(0xFF00D639) else Color(0xFFFFA000)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = setoran.createdAt?.take(10) ?: "-", fontSize = 12.sp, color = Color.Gray)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isDinilai) "Dinilai" else "Menunggu",
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Halaman ${setoran.halaman}", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            if (isDinilai) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Text("Nilai: ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "${setoran.nilai}", fontWeight = FontWeight.ExtraBold, color = Color(0xFF00D639))
                }
                Text(
                    text = "Catatan Ustadz: ${setoran.catatan ?: "-"}",
                    fontSize = 13.sp, color = Color.Gray
                )
            }
        }
    }
}