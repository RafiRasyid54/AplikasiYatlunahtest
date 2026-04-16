package com.yatlunah.app.ui.screen.materi

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────
// Token Warna Konsisten
// ─────────────────────────────────────────────
private object MenuBelajarColors {
    val brandGreen     = Color(0xFF00D639)
    val darkBackground = Color(0xFF0F0F0F)
    val darkSurface    = Color(0xFF1A1A1A)
    val lightBackground = Color(0xFFF4F5F7)
    val textSecondary  = Color(0xFFA0A0A0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuBelajarScreen(
    namaUser: String,
    onBack: () -> Unit,
    onNavigateToMateri: () -> Unit,
    onNavigateToRiwayat: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    // Theme Logic
    val bgColor = if (isDark) MenuBelajarColors.darkBackground else MenuBelajarColors.lightBackground
    val surfaceColor = if (isDark) MenuBelajarColors.darkSurface else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)
    val brandGreen = MenuBelajarColors.brandGreen

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Menu Belajar",
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section Header Gaya Dashboard
            Text(
                text = "KURIKULUM SAYA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) MenuBelajarColors.textSecondary else Color.Gray,
                letterSpacing = 1.sp
            )

            // 1. Materi Iqra
            BelajarMenuCard(
                title = "Materi Iqra",
                subtitle = "Baca PDF, dengarkan audio, dan rekam setoranmu.",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                accentColor = brandGreen,
                surfaceColor = surfaceColor,
                isDark = isDark,
                onClick = onNavigateToMateri
            )

            // 2. Riwayat Setoran
            BelajarMenuCard(
                title = "Riwayat Setoran",
                subtitle = "Lihat status penilaian dan catatan Ustadz.",
                icon = Icons.Default.AssignmentTurnedIn,
                accentColor = Color(0xFF007BFF),
                surfaceColor = surfaceColor,
                isDark = isDark,
                onClick = onNavigateToRiwayat
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Info Box Gaya Admin
            Surface(
                color = if (isDark) Color(0x0DFFFFFF) else Color(0x0D000000),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Tips: Pastikan sinyal stabil saat mengirimkan rekaman audio setoran hafalan Anda.",
                    fontSize = 12.sp,
                    color = if (isDark) MenuBelajarColors.textSecondary else Color.Gray,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun BelajarMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    surfaceColor: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val iconBg = if (isDark) accentColor.copy(alpha = 0.2f) else accentColor.copy(alpha = 0.1f)

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
                    .size(52.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isDark) Color.White else Color.Black
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
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