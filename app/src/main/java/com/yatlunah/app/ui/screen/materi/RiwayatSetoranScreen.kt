package com.yatlunah.app.ui.screen.materi

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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatlunah.app.data.model.Setoran
import com.yatlunah.app.data.remote.RetrofitClient

// ─────────────────────────────────────────────
// Token Warna Konsisten
// ─────────────────────────────────────────────
private object RiwayatColors {
    val brandGreen     = Color(0xFF00D639)
    val darkBackground = Color(0xFF0F0F0F)
    val darkSurface    = Color(0xFF1A1A1A)
    val lightBackground = Color(0xFFF4F5F7)
    val textSecondary  = Color(0xFFA0A0A0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatSetoranScreen(
    userId: String,
    onBack: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) RiwayatColors.darkBackground else RiwayatColors.lightBackground
    val surfaceColor = if (isDark) RiwayatColors.darkSurface else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)
    val brandGreen = RiwayatColors.brandGreen

    var selectedJilid by remember { mutableStateOf<Int?>(null) }
    var listRiwayat by remember { mutableStateOf<List<Setoran>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        try {
            val response = RetrofitClient.materiApi.getRiwayatSetoran(userId)
            if (response.isSuccessful) {
                listRiwayat = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            // Error handling
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (selectedJilid == null) "Riwayat Setoran" else "Jilid $selectedJilid",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textColor
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (selectedJilid != null) selectedJilid = null else onBack()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = textColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = surfaceColor
                    )
                )
                HorizontalDivider(
                    color = if (isDark) Color(0xFF2E2E2E) else Color(0xFFE5E5E5),
                    thickness = 0.5.dp
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = brandGreen
                )
            } else if (selectedJilid == null) {
                // --- DAFTAR JILID ---
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "PILIH JILID IQRA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) RiwayatColors.textSecondary else Color.Gray,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items((1..6).toList()) { nomor ->
                        FolderJilidCard(
                            nomor = nomor,
                            surfaceColor = surfaceColor,
                            brandGreen = brandGreen,
                            isDark = isDark
                        ) {
                            selectedJilid = nomor
                        }
                    }
                }
            } else {
                // --- DAFTAR SETORAN DI JILID TERSEBUT ---
                val filteredList = listRiwayat.filter { it.jilid == selectedJilid }

                if (filteredList.isEmpty()) {
                    Text(
                        text = "Belum ada riwayat setoran\ndi Jilid $selectedJilid",
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 14.sp
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredList) { setoran ->
                            RiwayatCard(
                                setoran = setoran,
                                surfaceColor = surfaceColor,
                                brandGreen = brandGreen,
                                isDark = isDark
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderJilidCard(
    nomor: Int,
    surfaceColor: Color,
    brandGreen: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(brandGreen.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = brandGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Riwayat Jilid $nomor",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isDark) Color.White else Color.Black,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}

@Composable
fun RiwayatCard(
    setoran: Setoran,
    surfaceColor: Color,
    brandGreen: Color,
    isDark: Boolean
) {
    val isDinilai = setoran.status == "dinilai"
    val statusColor = if (isDinilai) brandGreen else Color(0xFFFFA000)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = setoran.createdAt?.take(10) ?: "-",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (isDinilai) "DINILAI" else "MENUNGGU",
                        color = statusColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Halaman ${setoran.halaman}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (isDark) Color.White else Color.Black
            )

            if (isDinilai) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(
                    color = if (isDark) Color(0xFF2E2E2E) else Color(0xFFF0F0F0),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Skor: ",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${setoran.nilai}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = brandGreen
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Catatan: ${setoran.catatan ?: "Tidak ada catatan."}",
                    fontSize = 13.sp,
                    color = if (isDark) Color.LightGray else Color(0xFF555555),
                    lineHeight = 18.sp
                )
            }
        }
    }
}