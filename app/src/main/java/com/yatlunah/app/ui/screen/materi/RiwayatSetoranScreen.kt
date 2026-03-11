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

// ✅ Data Model
data class SetoranDummy(
    val jilid: Int,
    val halaman: Int,
    val tanggal: String,
    val status: String, // "Menunggu" atau "Dinilai"
    val nilai: Int?,
    val catatan: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatSetoranScreen(
    userId: String,
    onBack: () -> Unit
) {
    // ✅ State untuk melacak jilid mana yang sedang dibuka
    var selectedJilid by remember { mutableStateOf<Int?>(null) }

    // Simulasi data dari server (Nanti ditarik berdasarkan userId)
    val listRiwayat = listOf(
        SetoranDummy(1, 10, "10 Mar 2026", "Menunggu", null, null),
        SetoranDummy(1, 5, "08 Mar 2026", "Dinilai", 85, "Alhamdulillah lancar, perhatikan panjang pendeknya."),
        SetoranDummy(1, 1, "05 Mar 2026", "Dinilai", 90, "Sempurna! Lanjutkan."),
        SetoranDummy(2, 3, "12 Mar 2026", "Menunggu", null, null)
    )

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
                        // Jika sedang di dalam jilid, balik ke daftar jilid. Jika di daftar jilid, balik ke menu belajar.
                        if (selectedJilid != null) selectedJilid = null else onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F5F7))
                .padding(innerPadding)
        ) {
            if (selectedJilid == null) {
                // --- TAHAP 1: DAFTAR JILID ---
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            "Pilih Jilid untuk melihat penilaian:",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items((1..6).toList()) { nomor ->
                        FolderJilidCard(nomor = nomor) {
                            selectedJilid = nomor
                        }
                    }
                }
            } else {
                // --- TAHAP 2: DAFTAR SETORAN DI JILID TERSEBUT ---
                val filteredList = listRiwayat.filter { it.jilid == selectedJilid }

                if (filteredList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada setoran di Jilid $selectedJilid", color = Color.Gray)
                    }
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
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
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun RiwayatCard(setoran: SetoranDummy) {
    val isDinilai = setoran.status == "Dinilai"
    val statusColor = if (isDinilai) Color(0xFF00D639) else Color(0xFFFFA000)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = setoran.tanggal, fontSize = 12.sp, color = Color.Gray)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = setoran.status,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Halaman ${setoran.halaman}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.DarkGray
            )

            if (isDinilai) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Nilai: ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = "${setoran.nilai} / 100",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color(0xFF00D639)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Catatan Ustadz:\n\"${setoran.catatan}\"",
                    fontSize = 13.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}