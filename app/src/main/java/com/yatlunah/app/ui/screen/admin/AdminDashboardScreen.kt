package com.yatlunah.app.ui.screen.admin

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
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.List
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
// Token warna - Diselaraskan dengan Tema Santri
// ─────────────────────────────────────────────
private object AdminColors {
    val brandGreen      = Color(0xFF22C55E)
    val lightGreenBg    = Color(0xFFF0FDF4)
    val darkGreenCard   = Color(0xFF065F46)
    val textSecondary   = Color(0xFF6B7280)
    val darkBackground  = Color(0xFF0F172A)
    val darkSurface     = Color(0xFF1E293B)

    // Aksen spesifik admin
    val amber           = Color(0xFFF59E0B)
    val blue            = Color(0xFF3B82F6)
}

@Composable
fun AdminDashboardScreen(
    namaAdmin: String,
    onNavigateToUserMgmt: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    // Warna yang berubah sesuai tema
    val bgColor = if (isDark) AdminColors.darkBackground else AdminColors.lightGreenBg
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val surfaceColor = if (isDark) AdminColors.darkSurface else Color.White

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            viewModel.startRealtimeUpdates(context)
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        viewModel.startRealtimeUpdates(context)
        viewModel.fetchDashboardStats()
        viewModel.fetchRecentLogs()
    }

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            AdminBottomBar(
                isDark = isDark,
                brandGreen = AdminColors.brandGreen,
                onNavigateToUserMgmt = onNavigateToUserMgmt,
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
            AdminHeader(namaAdmin = namaAdmin, textColor = textColor)

            // ── WAKTU SHALAT BANNER (REALTIME) ───────────────────
            WaktuShalatBanner(
                hijriDate = viewModel.hijriDate,
                lokasi = viewModel.currentLocationName,
                sholatNama = viewModel.currentShalatName,
                sholatWaktu = viewModel.currentShalatTime
            )

            // ── 1. OVERVIEW (REALTIME) ────────────────────────────
            OverviewCard(
                totalUser = viewModel.totalPengguna.toString(),
                totalGuru = viewModel.totalGuru.toString(),
                totalSantri = viewModel.totalSantri.toString(),
                totalMitra = viewModel.totalMitra.toString()
            )

            // ── 3. MENU GRID ──────────────────────────────────────
            SectionLabel(text = "Menu Utama", isDark = isDark)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminQuickMenuCard(
                    modifier = Modifier.weight(1f),
                    title = "Manajemen User",
                    sub = "Kelola Akses",
                    icon = Icons.Default.Group,
                    color = AdminColors.brandGreen,
                    surfaceColor = surfaceColor,
                    textColor = textColor,
                    onClick = onNavigateToUserMgmt
                )
                AdminQuickMenuCard(
                    modifier = Modifier.weight(1f),
                    title = "Manajemen Materi",
                    sub = "Update PDF",
                    icon = Icons.AutoMirrored.Filled.LibraryBooks,
                    color = AdminColors.amber,
                    surfaceColor = surfaceColor,
                    textColor = textColor,
                    onClick = { /* TODO */ }
                )
                AdminQuickMenuCard(
                    modifier = Modifier.weight(1f),
                    title = "Laporan",
                    sub = "Rekap Nilai",
                    icon = Icons.Default.Assessment,
                    color = AdminColors.blue,
                    surfaceColor = surfaceColor,
                    textColor = textColor,
                    onClick = { /* TODO */ }
                )
            }

            // ── 2. LOG AKTIVITAS (REALTIME) ───────────────────────
            SectionLabel(text = "Aktivitas Terkini", isDark = isDark)
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    if (viewModel.recentLogs.isEmpty()) {
                        Text(
                            "Belum ada aktivitas.",
                            color = AdminColors.textSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        viewModel.recentLogs.forEachIndexed { index, log ->
                            val logColor = when (log.role) {
                                "guru" -> AdminColors.amber
                                "santri" -> AdminColors.blue
                                else -> AdminColors.brandGreen
                            }

                            AdminLogActivityRow(
                                nama = log.nama,
                                aksi = log.aksi,
                                waktu = log.waktu,
                                initials = log.initials,
                                iconColor = logColor,
                                textColor = textColor
                            )
                            if (index < viewModel.recentLogs.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = AdminColors.textSecondary.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }

            // ── 4. QUICK ACTION ───────────────────────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, top = 8.dp)
            ) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Pusat Kontrol Akses 🚀",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = textColor
                    )
                    Text(
                        "Kelola hak akses dan perizinan user dengan mudah dan cepat.",
                        fontSize = 13.sp,
                        color = AdminColors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    Button(
                        onClick = onNavigateToUserMgmt,
                        colors = ButtonDefaults.buttonColors(containerColor = AdminColors.brandGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Buka Manajemen User", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Sub-composable: Header
// ─────────────────────────────────────────────
@Composable
private fun AdminHeader(namaAdmin: String, textColor: Color) {
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
                color = AdminColors.brandGreen,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Admin $namaAdmin",
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
            .background(Brush.horizontalGradient(listOf(AdminColors.darkGreenCard, Color(0xFF064E3B))))
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
// Sub-composable: Overview Card (Diperbarui jadi kotak Grid)
// ─────────────────────────────────────────────
// ─────────────────────────────────────────────
// Sub-composable: Overview Card (Compact Grid 2x2)
// ─────────────────────────────────────────────
// ─────────────────────────────────────────────
// Sub-composable: Overview Card (Sejajar 4 Kolom - No Scroll)
// ─────────────────────────────────────────────
@Composable
private fun OverviewCard(totalUser: String, totalGuru: String, totalSantri: String, totalMitra: String) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AdminColors.brandGreen),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp) // Padding diperkecil agar ruang Row lebih luas
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            ) {
                Icon(Icons.Default.Analytics, null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Statistik Sistem", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // Menggunakan Row dengan Weight agar 4 item muat sejajar tanpa scroll
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp) // Jarak antar kotak sangat tipis
            ) {
                StatPill(modifier = Modifier.weight(1f), label = "Total", value = totalUser)
                StatPill(modifier = Modifier.weight(1f), label = "Guru", value = totalGuru)
                StatPill(modifier = Modifier.weight(1f), label = "Santri", value = totalSantri)
                StatPill(modifier = Modifier.weight(1f), label = "Mitra", value = totalMitra)
            }
        }
    }
}

@Composable
private fun StatPill(modifier: Modifier = Modifier, label: String, value: String) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp), // Padding sangat compact
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 9.sp, // Ukuran label kecil
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 14.sp, // Angka tetap menonjol
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
private fun AdminQuickMenuCard(
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
            Text(sub, fontSize = 10.sp, color = AdminColors.textSecondary, textAlign = TextAlign.Center)
        }
    }
}

// ─────────────────────────────────────────────
// Sub-composable: Section Label
// ─────────────────────────────────────────────
@Composable
private fun SectionLabel(text: String, isDark: Boolean) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Black,
        color = if (isDark) Color.White else Color(0xFF334155),
        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
    )
}

// ─────────────────────────────────────────────
// Sub-composable: Log Activity Row
// ─────────────────────────────────────────────
@Composable
fun AdminLogActivityRow(
    nama: String, aksi: String, waktu: String, initials: String,
    iconColor: Color, textColor: Color
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(40.dp).background(iconColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = initials, color = iconColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(nama, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(aksi, fontSize = 12.sp, color = AdminColors.textSecondary)
        }
        Text(waktu, fontSize = 10.sp, color = AdminColors.textSecondary)
    }
}

// ─────────────────────────────────────────────
// Sub-composable: Bottom Bar
// ─────────────────────────────────────────────
@Composable
private fun AdminBottomBar(
    isDark: Boolean, brandGreen: Color,
    onNavigateToUserMgmt: () -> Unit, onNavigateToProfile: () -> Unit
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
            icon = { Icon(Icons.Default.Home, null) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = brandGreen, indicatorColor = brandGreen.copy(0.1f))
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToUserMgmt,
            icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
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