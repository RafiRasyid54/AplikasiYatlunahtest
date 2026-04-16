package com.yatlunah.app.ui.screen.guru

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.data.model.Bimbingan

// ─────────────────────────────────────────────
// Token Warna - Menggunakan nama unik agar tidak bentrok
// ─────────────────────────────────────────────
private object BimbinganGuruColors {
    val brandGreen     = Color(0xFF00D639)
    val brandRed       = Color(0xFFFF3B30)
    val darkBackground = Color(0xFF0F0F0F)
    val darkSurface    = Color(0xFF1A1A1A)
    val lightBackground = Color(0xFFF4F5F7)
    val textSecondary  = Color(0xFFA0A0A0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruBimbinganScreen(
    idGuru: String,
    onBack: () -> Unit,
    viewModel: GuruBimbinganViewModel = viewModel()
) {
    val isDark = isSystemInDarkTheme()

    // Inisialisasi warna di dalam scope Composable
    val bgColor = if (isDark) BimbinganGuruColors.darkBackground else BimbinganGuruColors.lightBackground
    val surfaceColor = if (isDark) BimbinganGuruColors.darkSurface else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)

    val antreanList by viewModel.antreanList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAntrean()
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Permintaan Bimbingan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textColor
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = BimbinganGuruColors.brandGreen
                )
            } else if (antreanList.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Belum ada santri baru",
                        color = BimbinganGuruColors.textSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Daftar antrean akan muncul di sini",
                        color = BimbinganGuruColors.textSecondary.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "ANTREAN PENDAFTARAN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) BimbinganGuruColors.textSecondary else Color.Gray,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(antreanList) { bimbingan ->
                        ItemAntreanGuru(
                            bimbingan = bimbingan,
                            surfaceColor = surfaceColor,
                            isDark = isDark,
                            onTolak = { viewModel.updateStatus(bimbingan.id, "Ditolak", idGuru) },
                            onTerima = { viewModel.updateStatus(bimbingan.id, "Diterima", idGuru) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemAntreanGuru(
    bimbingan: Bimbingan,
    surfaceColor: Color,
    isDark: Boolean,
    onTolak: () -> Unit,
    onTerima: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = BimbinganGuruColors.brandGreen.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = BimbinganGuruColors.brandGreen,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ID Santri: ${bimbingan.userId}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isDark) Color.White else Color.Black
                    )
                    Text(
                        text = bimbingan.jenisBimbingan,
                        color = BimbinganGuruColors.brandGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = if (isDark) Color(0xFF2E2E2E) else Color(0xFFF0F0F0),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = bimbingan.tanggalDaftar?.take(10) ?: "-",
                    fontSize = 12.sp,
                    color = BimbinganGuruColors.textSecondary
                )

                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = onTolak,
                    colors = ButtonDefaults.textButtonColors(contentColor = BimbinganGuruColors.brandRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Tolak", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onTerima,
                    colors = ButtonDefaults.buttonColors(containerColor = BimbinganGuruColors.brandGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Text("Terima", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}