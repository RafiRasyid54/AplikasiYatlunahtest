package com.yatlunah.app.ui.screen.admin

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
// Token warna — satu tempat, mudah diubah
// ─────────────────────────────────────────────
private object DarkTokens {
    val background    = Color(0xFF0F0F0F) // bukan pure black → nyaman di OLED
    val surface1      = Color(0xFF1A1A1A) // kartu utama
    val surface2      = Color(0xFF242424) // kartu nested / hover
    val surface3      = Color(0xFF2E2E2E) // divider / chip
    val border        = Color(0x12FFFFFF) // rgba(255,255,255,0.07)
    val borderMedium  = Color(0x1FFFFFFF) // rgba(255,255,255,0.12)

    val green         = Color(0xFF22C55E) // aksen utama (tombol, aktif)
    val greenDim      = Color(0xFF166534) // overview card gradient start
    val greenDeep     = Color(0xFF14532D) // overview card gradient end
    val greenTint     = Color(0x1F22C55E) // background tinted hijau

    val amber         = Color(0xFFF59E0B)
    val amberTint     = Color(0x1AF59E0B)
    val blue          = Color(0xFF60A5FA)
    val blueTint      = Color(0x1A60A5FA)

    val text1         = Color(0xFFF0F0F0) // teks utama
    val text2         = Color(0xFFA0A0A0) // teks sekunder / section label
    val text3         = Color(0xFF606060) // teks tersier / timestamp
    val textOnGreen   = Color.White
}

private object LightTokens {
    val green         = Color(0xFF00D639)
    val greenDeep     = Color(0xFF00D639)
    val greenDim      = Color(0xFF00D639)
    val greenTint     = Color(0x1A00D639)
    val amber         = Color(0xFFF59E0B)
    val amberTint     = Color(0x1AF59E0B)
    val blue          = Color(0xFF3B82F6)
    val blueTint      = Color(0x1A3B82F6)
    val text2         = Color.Gray
    val text3         = Color.LightGray
    val border        = Color(0x22000000)
    val borderMedium  = Color(0x33000000)
}

@Composable
fun AdminDashboardScreen(
    namaAdmin: String,
    onNavigateToUserMgmt: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    // Warna yang berubah sesuai tema
    val brandGreen    = if (isDark) DarkTokens.green        else LightTokens.green
    val greenTint     = if (isDark) DarkTokens.greenTint    else LightTokens.greenTint
    val amberColor    = if (isDark) DarkTokens.amber        else LightTokens.amber
    val amberTint     = if (isDark) DarkTokens.amberTint    else LightTokens.amberTint
    val blueColor     = if (isDark) DarkTokens.blue         else LightTokens.blue
    val blueTint      = if (isDark) DarkTokens.blueTint     else LightTokens.blueTint
    val text2         = if (isDark) DarkTokens.text2        else LightTokens.text2
    val text3         = if (isDark) DarkTokens.text3        else LightTokens.text3
    val borderColor   = if (isDark) DarkTokens.border       else LightTokens.border
    val surface1Bg    = if (isDark) DarkTokens.surface1     else MaterialTheme.colorScheme.surface

    Scaffold(
        containerColor = if (isDark) DarkTokens.background else MaterialTheme.colorScheme.background,
        bottomBar = {
            AdminBottomBar(
                isDark = isDark,
                brandGreen = brandGreen,
                onNavigateToUserMgmt = onNavigateToUserMgmt,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) DarkTokens.background else MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // ── HEADER ──────────────────────────────────────────
            AdminHeader(namaAdmin = namaAdmin, isDark = isDark, brandGreen = brandGreen)

            // ── 1. OVERVIEW ──────────────────────────────────────
            OverviewCard(isDark = isDark)

            Spacer(Modifier.height(20.dp))

            // ── 2. LOG AKTIVITAS ─────────────────────────────────
            SectionLabel(text = "Log Aktivitas Terbaru", isDark = isDark)
            Spacer(Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = surface1Bg),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    // gunakan border tipis di dark mode
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp)) {
                    AdminLogActivityRow(
                        nama = "Admin A",
                        aksi = "Menambah Guru Baru",
                        waktu = "1 jam lalu",
                        initials = "AA",
                        avatarBg = greenTint,
                        avatarTextColor = brandGreen,
                        text2 = text2,
                        text3 = text3
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = borderColor
                    )
                    AdminLogActivityRow(
                        nama = "User B",
                        aksi = "Melakukan Registrasi",
                        waktu = "3 jam lalu",
                        initials = "UB",
                        avatarBg = blueTint,
                        avatarTextColor = blueColor,
                        text2 = text2,
                        text3 = text3
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── 3. MENU GRID ──────────────────────────────────────
            SectionLabel(text = "Menu", isDark = isDark)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminSmallMenuCard(
                    modifier = Modifier.weight(1f),
                    label = "Manajemen User",
                    sub = "Cek & Edit",
                    icon = Icons.Default.Group,
                    iconBg = greenTint,
                    iconTint = brandGreen,
                    isDark = isDark,
                    onClick = onNavigateToUserMgmt
                )
                AdminSmallMenuCard(
                    modifier = Modifier.weight(1f),
                    label = "Manajemen Materi",
                    sub = "Update PDF",
                    icon = Icons.Default.LibraryBooks,
                    iconBg = amberTint,
                    iconTint = amberColor,
                    isDark = isDark
                )
                AdminSmallMenuCard(
                    modifier = Modifier.weight(1f),
                    label = "Laporan",
                    sub = "Rekap Nilai",
                    icon = Icons.Default.Assessment,
                    iconBg = blueTint,
                    iconTint = blueColor,
                    isDark = isDark
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── 4. QUICK ACTION ───────────────────────────────────
            SectionLabel(text = "Kontrol Manajemen", isDark = isDark)
            Spacer(Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = surface1Bg),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(greenTint, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = brandGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Akses Cepat Admin",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        "Kelola hak akses dan perizinan user dalam satu klik.",
                        fontSize = 12.sp,
                        color = text2,
                        modifier = Modifier.padding(top = 8.dp),
                        lineHeight = 18.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToUserMgmt,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            "Buka Manajemen User \uD83D\uDC65",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
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
private fun AdminHeader(namaAdmin: String, isDark: Boolean, brandGreen: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Assalamualaikum,",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Text(
                "Admin $namaAdmin",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = if (isDark) DarkTokens.greenTint else Color(0xFFE8FFF0),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_yatlunah),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

// ─────────────────────────────────────────────
// Sub-composable: Overview Card
// ─────────────────────────────────────────────
@Composable
private fun OverviewCard(isDark: Boolean) {
    // Di dark mode: gradient gelap hijau (tidak neon)
    // Di light mode: hijau cerah solid
    val cardBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(DarkTokens.greenDeep, DarkTokens.greenDim)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFF00D639), Color(0xFF00C032))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = cardBrush, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                "Overview Status Sistem",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatPill(label = "Total", value = "1,300")
                StatPill(label = "Guru", value = "38")
                StatPill(label = "Peserta", value = "1,262")
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String) {
    Box(
        modifier = Modifier
            .background(Color(0x33000000), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            "$label: $value",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─────────────────────────────────────────────
// Sub-composable: Section Label
// ─────────────────────────────────────────────
@Composable
private fun SectionLabel(text: String, isDark: Boolean) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = if (isDark) DarkTokens.text2 else Color.Gray,
        letterSpacing = 0.8.sp
    )
}

// ─────────────────────────────────────────────
// Sub-composable: Log Activity Row
// ─────────────────────────────────────────────
@Composable
fun AdminLogActivityRow(
    nama: String,
    aksi: String,
    waktu: String,
    initials: String,
    avatarBg: Color,
    avatarTextColor: Color,
    text2: Color,
    text3: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar dengan inisial berwarna
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(avatarBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = avatarTextColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                nama,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(aksi, fontSize = 11.sp, color = text2)
        }
        Text(waktu, fontSize = 10.sp, color = text3)
    }
}

// ─────────────────────────────────────────────
// Sub-composable: Small Menu Card
// ─────────────────────────────────────────────
@Composable
fun AdminSmallMenuCard(
    modifier: Modifier,
    label: String,
    sub: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    isDark: Boolean,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkTokens.surface1 else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                label,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
            Text(
                sub,
                fontSize = 9.sp,
                color = if (isDark) DarkTokens.text3 else Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─────────────────────────────────────────────
// Sub-composable: Bottom Bar
// ─────────────────────────────────────────────
@Composable
private fun AdminBottomBar(
    isDark: Boolean,
    brandGreen: Color,
    onNavigateToUserMgmt: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val inactiveColor = if (isDark) DarkTokens.text3 else Color.Gray
    val barBg = if (isDark) DarkTokens.surface1 else Color.White

    NavigationBar(
        containerColor = barBg,
        tonalElevation = if (isDark) 0.dp else 8.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
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
                indicatorColor = if (isDark) DarkTokens.greenTint else Color(0xFFE8FFF0)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToUserMgmt,
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
                indicatorColor = Color.Transparent
            )
        )
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