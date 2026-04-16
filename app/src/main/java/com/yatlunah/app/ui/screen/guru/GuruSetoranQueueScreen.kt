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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.data.model.Setoran

// ─────────────────────────────────────────────
// Token Warna Identik
// ─────────────────────────────────────────────
private object QueueColors {
    val brandGreen     = Color(0xFF00D639)
    val darkBackground = Color(0xFF0F0F0F)
    val darkSurface    = Color(0xFF1A1A1A)
    val lightBackground = Color(0xFFF4F5F7)
    val textSecondary  = Color(0xFFA0A0A0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruSetoranQueueScreen(
    jilidTarget: Int,
    onBack: () -> Unit,
    onNavigateToPenilaian: (Setoran) -> Unit,
    viewModel: GuruViewModel = viewModel()
) {
    val isDark = isSystemInDarkTheme()

    // Theme Logic
    val bgColor = if (isDark) QueueColors.darkBackground else QueueColors.lightBackground
    val surfaceColor = if (isDark) QueueColors.darkSurface else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)
    val brandGreen = QueueColors.brandGreen

    val semuaSetoran by viewModel.antreanSetoran.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(jilidTarget) {
        viewModel.fetchAntrean(jilidTarget)
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Antrean Jilid $jilidTarget",
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
            } else if (semuaSetoran.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Alhamdulillah, antrean kosong",
                        color = QueueColors.textSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Semua setoran Jilid $jilidTarget telah divalidasi",
                        color = QueueColors.textSecondary.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "MENUNGGU VALIDASI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) QueueColors.textSecondary else Color.Gray,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(semuaSetoran) { itemSetoran ->
                        SetoranAntreanItem(
                            setoran = itemSetoran,
                            surfaceColor = surfaceColor,
                            brandGreen = brandGreen,
                            isDark = isDark,
                            onClick = { onNavigateToPenilaian(itemSetoran) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetoranAntreanItem(
    setoran: Setoran,
    surfaceColor: Color,
    brandGreen: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Circle dengan Initial Nama
            Surface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = brandGreen.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = (setoran.namaSantri ?: "S").take(1).uppercase(),
                        fontWeight = FontWeight.ExtraBold,
                        color = brandGreen,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = setoran.namaSantri ?: "Siswa",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isDark) Color.White else Color.Black
                )
                Surface(
                    color = brandGreen.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "Jilid ${setoran.jilid} • Hal ${setoran.halaman}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}