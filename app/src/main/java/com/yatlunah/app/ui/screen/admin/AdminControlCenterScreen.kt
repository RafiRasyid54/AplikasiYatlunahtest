package com.yatlunah.app.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminControlCenterScreen(
    onNavigateToUserMgmt: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToLaporan: () -> Unit,
    onNavigateToQuestions: () -> Unit, // ✅ Disatukan menjadi satu parameter
    onBack: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    val bgColor      = if (isDark) Color(0xFF0F0F0F) else Color(0xFFF4F5F7)
    val surfaceColor = if (isDark) Color(0xFF1A1A1A) else Color.White
    val titleColor   = if (isDark) Color(0xFFF0F0F0) else Color(0xFF111111)

    Scaffold(
        containerColor = bgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Pusat Kendali Admin",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = titleColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = titleColor
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = surfaceColor,
                    titleContentColor = titleColor
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))

            // 1. Manajemen Pengguna
            AdminHubCard("Manajemen Pengguna", "Kelola role Siswa & Guru.", Icons.Default.People, Color(0xFF2563EB), surfaceColor, isDark, onNavigateToUserMgmt)

            Spacer(Modifier.height(12.dp))

            // 2. Kelola Latihan Soal (Gabungan Mapping & Monitoring) ✅
            AdminHubCard(
                title = "Kelola Latihan Soal",
                desc = "Input, edit, & monitoring bank soal latihan.",
                icon = Icons.Default.Assignment,
                accentColor = Color(0xFF00D639), // Hijau Brand
                surfaceColor = surfaceColor,
                isDark = isDark,
                onClick = onNavigateToQuestions
            )

            Spacer(Modifier.height(12.dp))

            // 3. Kutipan Harian
            AdminHubCard("Kutipan Harian", "Atur quotes inspiratif.", Icons.Default.FormatQuote, Color(0xFF16A34A), surfaceColor, isDark, onNavigateToQuotes)

            Spacer(Modifier.height(12.dp))

            // 4. Laporan Aktivitas
            AdminHubCard("Laporan Aktivitas", "Statistik & progres santri.", Icons.Default.Assessment, Color(0xFF6366F1), surfaceColor, isDark, onNavigateToLaporan)

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AdminHubCard(
    title: String,
    desc: String,
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
                modifier = Modifier.size(48.dp).background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if (isDark) Color.White else Color.Black)
                Text(desc, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray)
        }
    }
}