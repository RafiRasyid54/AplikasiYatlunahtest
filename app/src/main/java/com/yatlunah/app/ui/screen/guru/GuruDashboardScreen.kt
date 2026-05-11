package com.yatlunah.app.ui.screen.guru

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatlunah.app.R

// Menggunakan Token Warna yang konsisten dengan SantriDashboard
private object GuruColors {
    val brandGreen      = Color(0xFF22C55E)
    val lightGreenBg    = Color(0xFFF0FDF4)
    val darkGreenCard   = Color(0xFF065F46)
    val textSecondary   = Color(0xFF6B7280)
    val darkBackground  = Color(0xFF0F172A)
    val darkSurface     = Color(0xFF1E293B)
}

@Composable
fun GuruDashboardScreen(
    namaGuru: String,
    onNavigateToAntrean: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) GuruColors.darkBackground else GuruColors.lightGreenBg
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val surfaceColor = if (isDark) GuruColors.darkSurface else Color.White

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            GuruBottomBar(
                onNavigateToAntrean = onNavigateToAntrean,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Selamat Mengajar,",
                        fontSize = 14.sp,
                        color = GuruColors.brandGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ustadz $namaGuru",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.yatlunahlogo),
                    contentDescription = "Logo Yatlunah",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            // --- 1. OVERVIEW KELOMPOK (Banner Style) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.horizontalGradient(listOf(GuruColors.darkGreenCard, Color(0xFF064E3B))))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Kelompok Bimbingan Anda",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatPill(label = "Total Santri", value = "10")
                        StatPill(label = "Setoran Baru", value = "3")
                    }
                }
            }

            // --- 2. MENU NAVIGASI (Grid Style) ---
            SectionHeader("Menu Navigasi", isDark)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickMenuCard(
                    modifier = Modifier.weight(1f),
                    title = "Monitoring",
                    sub = "Cek Progres",
                    icon = Icons.Default.BarChart,
                    color = Color(0xFF3B82F6),
                    isDark = isDark,
                    onClick = { /* Implementasi */ }
                )
                QuickMenuCard(
                    modifier = Modifier.weight(1f),
                    title = "Koreksi",
                    sub = "Beri Nilai",
                    icon = Icons.AutoMirrored.Filled.FactCheck,
                    color = GuruColors.brandGreen,
                    isDark = isDark,
                    onClick = onNavigateToAntrean
                )
            }

            // --- 3. TUGAS SEGERA (Action Card) ---
            SectionHeader("Tugas Kelompok", isDark)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(GuruColors.brandGreen.copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Group, null, tint = GuruColors.brandGreen)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Validasi Setoran Santri",
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                    Text(
                        text = "Ada 3 setoran dari anggota kelompok bimbingan Anda yang memerlukan koreksi ustadz.",
                        fontSize = 13.sp,
                        color = GuruColors.textSecondary,
                        modifier = Modifier.padding(vertical = 12.dp),
                        lineHeight = 18.sp
                    )
                    Button(
                        onClick = onNavigateToAntrean,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GuruColors.brandGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Mulai Koreksi Sekarang", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────
// UI HELPERS (Sub-Composables)
// ─────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, isDark: Boolean) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Black,
        color = if (isDark) Color.White else Color(0xFF334155),
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun StatPill(label: String, value: String) {
    Box(
        modifier = Modifier
            .background(Color(0x33000000), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text("$label: $value", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun QuickMenuCard(
    modifier: Modifier,
    title: String,
    sub: String,
    icon: ImageVector,
    color: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.shadow(8.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = if(isDark) GuruColors.darkSurface else Color.White),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if(isDark) Color.White else Color.Black)
            Text(sub, fontSize = 11.sp, color = GuruColors.textSecondary)
        }
    }
}

@Composable
private fun GuruBottomBar(
    onNavigateToAntrean: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val brandGreen = GuruColors.brandGreen

    NavigationBar(
        containerColor = if(isDark) Color(0xFF161616) else Color.White,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { /* Home */ },
            icon = { Icon(Icons.Default.Home, null) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = brandGreen,
                indicatorColor = brandGreen.copy(0.1f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToAntrean,
            icon = { Icon(Icons.AutoMirrored.Filled.FactCheck, null) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray)
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToProfile,
            icon = { Icon(Icons.Default.Person, null) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray)
        )
    }
}