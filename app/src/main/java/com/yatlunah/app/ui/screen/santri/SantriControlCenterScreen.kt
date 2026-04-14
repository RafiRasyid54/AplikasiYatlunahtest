package com.yatlunah.app.ui.screen.santri

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private object ControlColors {
    val brandGreen     = Color(0xFF00D639)
    val darkBackground = Color(0xFF0F0F0F)
    val darkSurface    = Color(0xFF1A1A1A)
    val lightBackground = Color(0xFFF4F5F7)
    val textSecondary  = Color(0xFFA0A0A0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SantriControlCenterScreen(
    userId: String,
    namaUser: String,
    emailUser: String,
    navController: NavController,
    onNavigateToMateri: () -> Unit,
    onNavigateToRiwayat: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) ControlColors.darkBackground else ControlColors.lightBackground
    val surfaceColor = if (isDark) ControlColors.darkSurface else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)
    val brandGreen = ControlColors.brandGreen

    Scaffold(
        containerColor = bgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Menu Santri", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = surfaceColor)
            )
        },
        bottomBar = {
            // ✅ PERBAIKAN: Parameter disesuaikan dengan definisi fungsi di bawah
            SantriBottomBar(
                isDark = isDark,
                brandGreen = brandGreen,
                onNavigateToDashboard = {
                    navController.navigate("dashboard_santri/$userId/$namaUser/$emailUser") {
                        popUpTo("dashboard_santri/{id}/{nama}/{email}") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToJilid = { /* Sudah di sini */ },
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "LAYANAN AKADEMIK",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) ControlColors.textSecondary else Color.Gray,
                letterSpacing = 1.sp
            )

            ControlMenuCard("Materi & Setoran", "Baca PDF jilid dan kirim hafalan.", Icons.AutoMirrored.Filled.MenuBook, brandGreen, surfaceColor, isDark, onNavigateToMateri)
            ControlMenuCard("Bimbingan Saya", "Konsultasi tatap muka dengan Guru.", Icons.Default.Groups, Color(0xFF00BCC9), surfaceColor, isDark, {
                navController.navigate("santri_bimbingan_detail/$userId/$namaUser/$emailUser")
            })
            ControlMenuCard("Riwayat Setoran", "Lihat catatan progres dari Guru.", Icons.Default.History, Color(0xFFFF9800), surfaceColor, isDark, onNavigateToRiwayat)

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = if (isDark) Color(0x0DFFFFFF) else Color(0x0D000000),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Gunakan menu ini untuk mengakses fitur pembelajaran harian Anda di Yatlunah.",
                    fontSize = 12.sp,
                    color = if (isDark) ControlColors.textSecondary else Color.Gray,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ControlMenuCard(title: String, subtitle: String, icon: ImageVector, accentColor: Color, surfaceColor: Color, isDark: Boolean, onClick: () -> Unit) {
    val iconBg = if (isDark) accentColor.copy(alpha = 0.2f) else accentColor.copy(alpha = 0.1f)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(iconBg, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if (isDark) Color.White else Color.Black)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun SantriBottomBar(
    isDark: Boolean,
    brandGreen: Color,
    onNavigateToDashboard: () -> Unit,
    onNavigateToJilid: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    // Gunakan warna background yang sama dengan dashboard
    val barBg = if (isDark) Color(0xFF161616) else Color.White
    val inactiveColor = Color.Gray

    NavigationBar(
        containerColor = barBg,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        tonalElevation = 8.dp
    ) {
        // ITEM 1: HOME
        NavigationBarItem(
            selected = false, // Tidak aktif karena kita di Control Center
            onClick = onNavigateToDashboard,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = inactiveColor,
                indicatorColor = Color.Transparent
            )
        )

        // ITEM 2: MENU (Gunakan MenuBook agar sama dengan Dashboard)
        NavigationBarItem(
            selected = true, // AKTIF di screen ini
            onClick = onNavigateToJilid,
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Menu") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = brandGreen,
                // Indikator oval hijau transparan
                indicatorColor = brandGreen.copy(alpha = 0.1f)
            )
        )

        // ITEM 3: PROFIL
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToProfile,
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = inactiveColor,
                indicatorColor = Color.Transparent
            )
        )
    }
}