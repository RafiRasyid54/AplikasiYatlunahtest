package com.yatlunah.app.ui.screen.bimbingan

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// ─────────────────────────────────────────────
// Token Warna Konsisten
// ─────────────────────────────────────────────
private object DaftarBimbinganColors {
    val brandGreen     = Color(0xFF00D639)
    val darkBackground = Color(0xFF0F0F0F)
    val darkSurface    = Color(0xFF1A1A1A)
    val lightBackground = Color(0xFFF4F5F7)
    val textSecondary  = Color(0xFFA0A0A0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarBimbinganScreen(
    userId: String,
    totalProgress: Int,
    onBack: () -> Unit,
    viewModel: BimbinganViewModel = viewModel(factory = BimbinganViewModel.Factory)
) {
    val isDark = isSystemInDarkTheme()

    // Theme Logic
    val bgColor = if (isDark) DaftarBimbinganColors.darkBackground else DaftarBimbinganColors.lightBackground
    val surfaceColor = if (isDark) DaftarBimbinganColors.darkSurface else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)
    val brandGreen = DaftarBimbinganColors.brandGreen

    // Logika Kelayakan
    val isReady = totalProgress >= 80

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Pendaftaran Bimbingan",
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- 1. CARD STATUS PROGRES ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Progres Belajar Anda",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Capaian Saat Ini: $totalProgress%",
                        fontSize = 14.sp,
                        color = if (isDark) DaftarBimbinganColors.textSecondary else Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Status Indicator
                    val statusBg = if (isReady) {
                        if (isDark) Color(0x1A22C55E) else Color(0xFFE8F5E9)
                    } else {
                        if (isDark) Color(0x1ADC2626) else Color(0xFFFFEBEE)
                    }
                    val statusText = if (isReady) Color(0xFF22C55E) else Color(0xFFEF4444)

                    Surface(
                        color = statusBg,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isReady) "✓ Syarat Terpenuhi" else "⚠ Syarat Minimal: 80%",
                                color = statusText,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // --- 2. TOMBOL AKSI ---
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { viewModel.submitPendaftaran(userId, "Online") },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = isReady && viewModel.uiState !is BimbinganUiState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = brandGreen,
                        contentColor = Color.White,
                        disabledContainerColor = if (isDark) Color(0xFF242424) else Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Daftar Bimbingan Online", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.submitPendaftaran(userId, "Tatap Muka") },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = isReady && viewModel.uiState !is BimbinganUiState.Loading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isDark) brandGreen else brandGreen
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(if (isReady) brandGreen else Color.Gray)
                    )
                ) {
                    Text("Daftar Bimbingan Tatap Muka", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            // --- 3. FEEDBACK STATUS ---
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val state = viewModel.uiState) {
                    is BimbinganUiState.Loading -> CircularProgressIndicator(color = brandGreen)
                    is BimbinganUiState.Success -> {
                        Surface(
                            color = brandGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Berhasil Terdaftar! Silakan tunggu konfirmasi Guru.",
                                color = if (isDark) brandGreen else Color(0xFF1B5E20),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    is BimbinganUiState.Error -> {
                        Text(
                            state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    }
                    else -> {
                        Text(
                            "Anda hanya dapat mendaftar jika progres belajar telah mencapai minimal 80%.",
                            fontSize = 12.sp,
                            color = if (isDark) DaftarBimbinganColors.textSecondary else Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}