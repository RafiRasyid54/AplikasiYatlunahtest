package com.yatlunah.app.ui.screen.guru

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
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

// ─────────────────────────────────────────────
// Token Warna - Identik dengan Dashboard Lain
// ─────────────────────────────────────────────
private object GuruColors {
    val brandGreen      = Color(0xFF00D639)
    val darkGreenDeep   = Color(0xFF14532D)
    val darkGreenDim    = Color(0xFF166534)
    val darkBackground  = Color(0xFF0F0F0F)
    val darkSurface     = Color(0xFF1A1A1A)
    val lightBackground = Color(0xFFF4F5F7)
    val textSecondary   = Color(0xFFA0A0A0)
}

@Composable
fun GuruDashboardScreen(
    namaGuru: String,
    onNavigateToAntrean: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    // Logic Tema
    val bgColor = if (isDark) GuruColors.darkBackground else GuruColors.lightBackground
    val surfaceColor = if (isDark) GuruColors.darkSurface else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)
    val brandGreen = GuruColors.brandGreen

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            GuruBottomBar(
                isDark = isDark,
                brandGreen = brandGreen,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Assalamualaikum,", fontSize = 14.sp, color = if(isDark) GuruColors.textSecondary else Color.Gray)
                    Text("Ustadz $namaGuru", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
                }
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(if (isDark) Color(0x1F22C55E) else Color(0xFFE8FFF0), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_yatlunah),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                    )
                }
            }

            // --- 1. OVERVIEW CARD (Gradient Identik) ---
            val overviewBrush = Brush.linearGradient(
                colors = if (isDark) listOf(GuruColors.darkGreenDeep, GuruColors.darkGreenDim)
                else listOf(Color(0xFF00D639), Color(0xFF00C032))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = overviewBrush, shape = RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Text("Ringkasan Santri", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatPill(label = "Total Santri", value = "125")
                        StatPill(label = "Butuh Koreksi", value = "8")
                    }
                }
            }

            // --- 2. AKTIVITAS TERBARU ---
            SectionHeader("Aktivitas Santri", isDark)
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    AktivitasRowModern("Siswa A", "Menyelesaikan Jilid 2", "5m", brandGreen, isDark)
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = if(isDark) Color(0xFF2E2E2E) else Color(0xFFF0F0F0)
                    )
                    AktivitasRowModern("Siswa B", "Mendaftarkan Bimbingan", "20m", Color(0xFF00BCC9), isDark)
                }
            }

            // --- 3. MENU UTAMA (Grid Identik Admin) ---
            SectionHeader("Menu Navigasi", isDark)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SmallMenuCardModern(Modifier.weight(1f), "Monitoring", "Statistik", Icons.Default.BarChart, Color(0xFF3B82F6), isDark)
                SmallMenuCardModern(Modifier.weight(1f), "Koreksi", "12 Setoran", Icons.Default.FolderOpen, brandGreen, isDark, onNavigateToAntrean)
                SmallMenuCardModern(Modifier.weight(1f), "Feedback", "3 Pesan", Icons.Default.Chat, Color(0xFFFFA000), isDark)
            }

            // --- 4. QUICK ACTION SECTION ---
            SectionHeader("Tugas Segera", isDark)
            Card(
                onClick = onNavigateToAntrean,
                modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).background(brandGreen.copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PendingActions, null, tint = brandGreen, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("Validasi Antrean Setoran", fontWeight = FontWeight.Bold, color = textColor)
                    }
                    Text(
                        "Ada 12 setoran santri yang menunggu untuk Anda koreksi dan beri nilai.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 10.dp),
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToAntrean,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Buka Antrean ✅", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// UI HELPERS (Sub-Composables)
// ─────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, isDark: Boolean) {
    Text(
        text = title.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = if (isDark) GuruColors.textSecondary else Color.Gray,
        letterSpacing = 1.sp,
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
private fun AktivitasRowModern(nama: String, aksi: String, waktu: String, accent: Color, isDark: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(38.dp).background(accent.copy(0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(nama.take(1), color = accent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(nama, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if(isDark) Color.White else Color.Black)
            Text(aksi, fontSize = 12.sp, color = Color.Gray)
        }
        Text(waktu, fontSize = 10.sp, color = if(isDark) GuruColors.textSecondary else Color.LightGray)
    }
}

@Composable
private fun SmallMenuCardModern(modifier: Modifier, label: String, sub: String, icon: ImageVector, color: Color, isDark: Boolean, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = if(isDark) GuruColors.darkSurface else Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 2.dp)
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(36.dp).background(color.copy(0.1f), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(label, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, color = if(isDark) Color.White else Color.Black)
            Text(sub, fontSize = 9.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun GuruBottomBar(
    isDark: Boolean,
    brandGreen: Color,
    onNavigateToAntrean: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    // Penyesuaian warna agar sama dengan layar Santri/Admin
    val barBg = if (isDark) Color(0xFF161616) else Color.White
    val inactiveColor = if (isDark) Color(0xFF505050) else Color.Gray

    NavigationBar(
        containerColor = barBg,
        // Efek lengkungan yang sama di bagian atas
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        tonalElevation = if (isDark) 0.dp else 8.dp
    ) {
        // ITEM 1: HOME (SELECTED)
        NavigationBarItem(
            selected = true,
            onClick = { /* Sudah di Home */ },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = null, // Menghilangkan teks agar Apple-style minimalis
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = brandGreen,
                unselectedIconColor = inactiveColor,
                // Menggunakan Green Tint lembut saat terpilih
                indicatorColor = if (isDark) Color(0x1422C55E) else Color(0xFFE8FFF0)
            )
        )

        // ITEM 2: ANTREAN (ICON KHUSUS GURU)
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToAntrean,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.FactCheck,
                    contentDescription = "Antrean",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = null,
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = inactiveColor,
                indicatorColor = Color.Transparent
            )
        )

        // ITEM 3: PROFIL
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToProfile,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profil",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = null,
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = inactiveColor,
                indicatorColor = Color.Transparent
            )
        )
    }
}