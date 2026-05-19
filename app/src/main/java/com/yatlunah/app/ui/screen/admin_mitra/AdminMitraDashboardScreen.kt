package com.yatlunah.app.ui.screen.admin_mitra

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.R

// ─────────────────────────────────────────────
// Token warna
// ─────────────────────────────────────────────
private object MitraColors {
    val brandGreen      = Color(0xFF22C55E)
    val lightGreenBg    = Color(0xFFF0FDF4)
    val darkGreenCard   = Color(0xFF065F46)
    val textSecondary   = Color(0xFF6B7280)
    val darkBackground  = Color(0xFF0F172A)
    val darkSurface     = Color(0xFF1E293B)

    val amber           = Color(0xFFF59E0B)
    val blue            = Color(0xFF3B82F6)
    val red             = Color(0xFFEF4444)
}

@Composable
fun AdminMitraDashboardScreen(
    idMitra: String,
    namaAdmin: String,
    onNavigateToControl: () -> Unit,
    onNavigateToUserList: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    viewModel: MitraViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val bgColor = if (isDark) MitraColors.darkBackground else MitraColors.lightGreenBg
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val surfaceColor = if (isDark) MitraColors.darkSurface else Color.White

    // Launcher untuk Izin Lokasi (Waktu Shalat)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            viewModel.startRealtimeUpdates(context)
        }
    }

    // Trigger pemanggilan API saat layar dibuka
    LaunchedEffect(idMitra) {
        permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        viewModel.startRealtimeUpdates(context)
        viewModel.fetchDashboardData(idMitra)
    }

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            MitraBottomBar(
                isDark = isDark,
                brandGreen = MitraColors.brandGreen,
                onNavigateToControl = onNavigateToControl,
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
            // ── HEADER ──────────────────────────────────────────
            MitraHeader(
                namaAdmin = namaAdmin,
                namaLembaga = viewModel.namaLembaga,
                textColor = textColor
            )

            // ── WAKTU SHALAT BANNER (REALTIME) ───────────────────
            WaktuShalatBanner(
                hijriDate = viewModel.hijriDate,
                lokasi = viewModel.currentLocationName,
                sholatNama = viewModel.currentShalatName,
                sholatWaktu = viewModel.currentShalatTime
            )

            // ── OVERVIEW (STATISTIK MITRA) ───────────────────────
            MitraOverviewCard(
                namaLembaga = viewModel.namaLembaga,
                totalUser = viewModel.totalUserLembaga.toString(),
                totalGuru = viewModel.totalGuruLembaga.toString(),
                totalSantri = viewModel.totalSantriLembaga.toString(),
                isDark = isDark
            )

            // ── MENU UTAMA (Manajemen Lembaga) ───────────────────
            MitraSectionLabel(text = "Manajemen Lembaga", isDark = isDark)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MitraQuickMenuCard(
                    modifier = Modifier.weight(1f),
                    title = "Daftar Guru",
                    sub = "Kelola Pengajar",
                    icon = Icons.Default.PersonSearch,
                    color = MitraColors.blue,
                    surfaceColor = surfaceColor,
                    textColor = textColor,
                    onClick = { onNavigateToUserList("guru") }
                )
                MitraQuickMenuCard(
                    modifier = Modifier.weight(1f),
                    title = "Daftar Santri",
                    sub = "Data Peserta",
                    icon = Icons.Default.People,
                    color = MitraColors.brandGreen,
                    surfaceColor = surfaceColor,
                    textColor = textColor,
                    onClick = { onNavigateToUserList("santri") }
                )
            }

            // ── SISTEM & SESI ─────────────────────────────────────
            MitraSectionLabel(text = "Sistem & Sesi", isDark = isDark)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MitraQuickMenuCard(
                    modifier = Modifier.weight(1f),
                    title = "Profil Lembaga",
                    sub = "Detail Mitra",
                    icon = Icons.Default.CorporateFare,
                    color = MitraColors.amber,
                    surfaceColor = surfaceColor,
                    textColor = textColor,
                    onClick = { /* TODO */ }
                )
                MitraQuickMenuCard(
                    modifier = Modifier.weight(1f),
                    title = "Keluar",
                    sub = "Akhiri Sesi",
                    icon = Icons.AutoMirrored.Filled.Logout,
                    color = MitraColors.red,
                    surfaceColor = surfaceColor,
                    textColor = textColor,
                    onClick = onLogout
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

// ─────────────────────────────────────────────
// Sub-composable: Waktu Shalat Banner
// ─────────────────────────────────────────────
@Composable
private fun WaktuShalatBanner(hijriDate: String, lokasi: String, sholatNama: String, sholatWaktu: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(listOf(MitraColors.darkGreenCard, Color(0xFF064E3B))))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(hijriDate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(12.dp), tint = Color.LightGray)
                    Spacer(Modifier.width(4.dp))
                    Text(lokasi, fontSize = 11.sp, color = Color.LightGray, maxLines = 1)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(sholatNama, fontSize = 12.sp, color = Color.White.copy(0.8f))
                Text(sholatWaktu, fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }
    }
}

// ─────────────────────────────────────────────
// Sub-composable: Header
// ─────────────────────────────────────────────
@Composable
private fun MitraHeader(namaAdmin: String, namaLembaga: String, textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Assalamualaikum,",
                fontSize = 14.sp,
                color = MitraColors.brandGreen,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Admin $namaAdmin",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
            Text(
                namaLembaga,
                fontSize = 13.sp,
                color = MitraColors.textSecondary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
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
}

// ─────────────────────────────────────────────
// Sub-composable: Overview Card (Sejajar Tanpa Scroll)
// ─────────────────────────────────────────────
@Composable
private fun MitraOverviewCard(
    namaLembaga: String,
    totalUser: String,
    totalGuru: String,
    totalSantri: String,
    isDark: Boolean
) {
    val cardBrush = if (isDark) {
        Brush.linearGradient(listOf(MitraColors.darkGreenCard, Color(0xFF064E3B)))
    } else {
        Brush.linearGradient(listOf(MitraColors.brandGreen, Color(0xFF16A34A)))
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = cardBrush)
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Statistik $namaLembaga", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MitraStatPill(modifier = Modifier.weight(1f), label = "Total User", value = totalUser)
                    MitraStatPill(modifier = Modifier.weight(1f), label = "Total Guru", value = totalGuru)
                    MitraStatPill(modifier = Modifier.weight(1f), label = "Total Santri", value = totalSantri)
                }
            }
        }
    }
}

@Composable
private fun MitraStatPill(modifier: Modifier = Modifier, label: String, value: String) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
        }
    }
}

// ─────────────────────────────────────────────
// Sub-composable: Menu Card
// ─────────────────────────────────────────────
@Composable
private fun MitraQuickMenuCard(
    modifier: Modifier, title: String, sub: String, icon: ImageVector,
    color: Color, surfaceColor: Color, textColor: Color, onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.shadow(4.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textColor, textAlign = TextAlign.Center)
            Text(sub, fontSize = 10.sp, color = MitraColors.textSecondary, textAlign = TextAlign.Center)
        }
    }
}

// ─────────────────────────────────────────────
// Sub-composable: Section Label
// ─────────────────────────────────────────────
@Composable
private fun MitraSectionLabel(text: String, isDark: Boolean) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Black,
        color = if (isDark) Color.White else Color(0xFF334155),
        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
    )
}

// ─────────────────────────────────────────────
// Sub-composable: Bottom Bar
// ─────────────────────────────────────────────
@Composable
private fun MitraBottomBar(
    isDark: Boolean, brandGreen: Color,
    onNavigateToControl: () -> Unit, onNavigateToProfile: () -> Unit
) {
    val barBg = if(isDark) Color(0xFF161616) else Color.White

    NavigationBar(
        containerColor = barBg,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Default.Home, null, modifier = Modifier.size(26.dp)) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = brandGreen, indicatorColor = brandGreen.copy(0.1f))
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToControl,
            icon = { Icon(Icons.AutoMirrored.Filled.List, null, tint = Color.Gray, modifier = Modifier.size(24.dp)) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToProfile,
            icon = { Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(24.dp)) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent)
        )
    }
}