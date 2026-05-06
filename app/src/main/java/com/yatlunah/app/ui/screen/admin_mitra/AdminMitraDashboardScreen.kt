package com.yatlunah.app.ui.screen.admin_mitra

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List // ✅ Fix Deprecated
import androidx.compose.material.icons.automirrored.filled.Logout // ✅ Fix Deprecated
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatlunah.app.R
import com.yatlunah.app.ui.screen.admin_mitra.MitraTokens.brandGreen

private object MitraTokens {
    val darkBg = Color(0xFF0F0F0F)
    val darkSurface = Color(0xFF1A1A1A)
    val brandGreen = Color(0xFF00D639)
    val greenDeep = Color(0xFF14532D)
    val greenDim = Color(0xFF166534)
    val blueColor = Color(0xFF3B82F6)
}

@Composable
fun AdminMitraDashboardScreen(
    namaAdmin: String,
    onNavigateToControl: () -> Unit, // ✅ Gunakan ini untuk tombol tengah
    onNavigateToUserList: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) MitraTokens.darkBg else Color(0xFFF4F5F7)
    val surfaceColor = if (isDark) MitraTokens.darkSurface else Color.White

    // Di dalam AdminMitraDashboardScreen (Scaffold)
    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            MitraBottomBar(
                isDark = isDark,
                brandGreen = brandGreen, // ✅ Kirim variabel warna hijau
                onNavigateToControl = onNavigateToControl,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            MitraHeader(namaAdmin = namaAdmin, isDark = isDark)
            MitraOverviewCard(isDark = isDark)

            Spacer(Modifier.height(24.dp))

            MitraSectionLabel(text = "Manajemen User Lembaga", isDark = isDark)
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MitraSmallMenuCard(
                    modifier = Modifier.weight(1f),
                    label = "Daftar Guru",
                    sub = "Kelola Pengajar",
                    icon = Icons.Default.PersonSearch,
                    iconBg = MitraTokens.blueColor.copy(alpha = 0.1f),
                    iconTint = MitraTokens.blueColor,
                    isDark = isDark,
                    surfaceColor = surfaceColor, // ✅ Fix Unused surfaceColor
                    onClick = { onNavigateToUserList("guru") }
                )

                MitraSmallMenuCard(
                    modifier = Modifier.weight(1f),
                    label = "Daftar Santri",
                    sub = "Data Peserta",
                    icon = Icons.Default.People,
                    iconBg = MitraTokens.brandGreen.copy(alpha = 0.1f),
                    iconTint = MitraTokens.brandGreen,
                    isDark = isDark,
                    surfaceColor = surfaceColor,
                    onClick = { onNavigateToUserList("santri") }
                )
            }

            Spacer(Modifier.height(20.dp))

            MitraSectionLabel(text = "Sistem & Sesi", isDark = isDark)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MitraSmallMenuCard(
                    modifier = Modifier.weight(1f),
                    label = "Profil Lembaga",
                    sub = "Detail Mitra",
                    icon = Icons.Default.CorporateFare,
                    iconBg = Color(0xFFF59E0B).copy(alpha = 0.1f),
                    iconTint = Color(0xFFF59E0B),
                    isDark = isDark,
                    surfaceColor = surfaceColor
                )
                MitraSmallMenuCard(
                    modifier = Modifier.weight(1f),
                    label = "Keluar",
                    sub = "Akhiri Sesi",
                    icon = Icons.AutoMirrored.Filled.Logout, // ✅ Fix Deprecated
                    iconBg = Color.Red.copy(alpha = 0.1f),
                    iconTint = Color.Red,
                    isDark = isDark,
                    surfaceColor = surfaceColor,
                    onClick = onLogout
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── KOMPONEN PENDUKUNG ──────────────────────────────────────────────

@Composable
private fun MitraHeader(namaAdmin: String, isDark: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Assalamualaikum,", fontSize = 14.sp, color = if(isDark) Color.Gray else Color.DarkGray)
            Text("Admin $namaAdmin", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = if(isDark) Color.White else Color.Black)
            Text("Pengurus Lembaga", fontSize = 12.sp, color = MitraTokens.brandGreen)
        }
        Image(
            painter = painterResource(id = R.drawable.logo_yatlunah),
            contentDescription = null,
            modifier = Modifier.size(40.dp).clip(CircleShape)
        )
    }
}

@Composable
private fun MitraOverviewCard(isDark: Boolean) {
    val cardBrush = if (isDark) Brush.linearGradient(listOf(MitraTokens.greenDeep, MitraTokens.greenDim))
    else Brush.linearGradient(listOf(MitraTokens.brandGreen, Color(0xFF00C032))) // ✅ Fix Unused brandGreen

    Box(modifier = Modifier.fillMaxWidth().background(brush = cardBrush, shape = RoundedCornerShape(16.dp)).padding(16.dp)) {
        Column {
            Text("Status Lembaga", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))
            Box(Modifier.background(Color(0x33000000), RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                Text("Lembaga Aktif", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun MitraSectionLabel(text: String, isDark: Boolean) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp, fontWeight = FontWeight.Bold,
        color = if (isDark) Color.Gray else Color.DarkGray,
        letterSpacing = 0.8.sp
    )
}

@Composable
private fun MitraSmallMenuCard(
    modifier: Modifier, label: String, sub: String, icon: ImageVector,
    iconBg: Color, iconTint: Color, isDark: Boolean, surfaceColor: Color,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(36.dp).background(iconBg, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(label, fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center, color = if(isDark) Color.White else Color.Black)
            Text(sub, fontSize = 9.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun MitraBottomBar(
    isDark: Boolean,
    brandGreen: Color, // Pastikan parameter ini dikirim dari screen utama
    onNavigateToControl: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    // Menyesuaikan dengan variabel warna di AdminDashboardScreen
    val inactiveColor = if (isDark) Color(0xFF606060) else Color.Gray // DarkTokens.text3 / Color.Gray
    val barBg = if (isDark) Color(0xFF1A1A1A) else Color.White      // DarkTokens.surface1 / Color.White
    val indicatorColor = if (isDark) Color(0x1F22C55E) else Color(0xFFE8FFF0) // DarkTokens.greenTint

    NavigationBar(
        containerColor = barBg,
        tonalElevation = if (isDark) 0.dp else 8.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        // Tab Home (Aktif)
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(26.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = brandGreen,
                indicatorColor = indicatorColor // Menggunakan greenTint seperti Admin Utama
            )
        )

        // Tab Kontrol (Navigasi ke Control Screen)
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToControl,
            icon = {
                Icon(
                    Icons.Default.List,
                    contentDescription = "Daftar",
                    tint = inactiveColor,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = inactiveColor,
                indicatorColor = Color.Transparent // Menghilangkan lingkaran background tab tidak aktif
            )
        )

        // Tab Profil
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToProfile,
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profil",
                    tint = inactiveColor,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = inactiveColor,
                indicatorColor = Color.Transparent
            )
        )
    }
}