package com.yatlunah.app.ui.screen.santri

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.yatlunah.app.R
import java.util.*

// ─────────────────────────────────────────────
// Token Warna - Identik dengan Admin Dashboard
// ─────────────────────────────────────────────
private object SantriColors {
    val brandGreen     = Color(0xFF00D639)
    val darkGreenDeep  = Color(0xFF14532D) // Untuk gradient
    val darkGreenDim   = Color(0xFF166534)
    val darkBackground = Color(0xFF0F0F0F)
    val darkSurface    = Color(0xFF1A1A1A)
    val lightBackground = Color(0xFFF4F5F7)
    val textSecondary  = Color(0xFFA0A0A0)
}

@Composable
fun SantriDashboardScreen(
    userId: String,
    namaUser: String,
    emailUser: String,
    navController: NavController,
    onLogout: () -> Unit,
    onNavigateToJilid: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToBimbingan: () -> Unit,
    onNavigateToInfoProgram: () -> Unit,
    viewModel: SantriViewModel = viewModel()
) {
    val isDark = isSystemInDarkTheme()

    // Theme logic
    val bgColor = if (isDark) SantriColors.darkBackground else SantriColors.lightBackground
    val surfaceColor = if (isDark) SantriColors.darkSurface else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)
    val brandGreen = SantriColors.brandGreen

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.fetchStats(userId)
            viewModel.fetchStatusBimbingan(userId)
            viewModel.fetchQuoteBerdasarkanHari()
        }
    }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Selamat Pagi"
            in 12..14 -> "Selamat Siang"
            in 15..17 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            SantriBottomBar(
                isDark = isDark,
                brandGreen = brandGreen,
                onNavigateToDashboard = onNavigateToDashboard,
                onNavigateToJilid = onNavigateToJilid,
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

            // --- HEADER (Identik dengan Admin) ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("$greeting,", fontSize = 14.sp, color = if(isDark) SantriColors.textSecondary else Color.Gray)
                    Text(namaUser, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
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

            // --- 1. QUOTES CARD (Gradient identik dengan Overview Admin) ---
            val quoteBrush = if (isDark) {
                Brush.linearGradient(colors = listOf(SantriColors.darkGreenDeep, SantriColors.darkGreenDim))
            } else {
                Brush.linearGradient(colors = listOf(Color(0xFF00D639), Color(0xFF00C032)))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = quoteBrush, shape = RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FormatQuote, null, tint = Color.White.copy(0.7f), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Quotes Hari Ini", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"${viewModel.currentQuote}\"",
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // --- 2. PROGRESS SECTION ---
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Progres Belajar", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
                        Text("${(viewModel.progressPercent * 100).toInt()}%", color = brandGreen, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { viewModel.progressPercent },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = brandGreen,
                        trackColor = if(isDark) Color(0xFF2E2E2E) else Color(0xFFF0F0F0)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MiniStat(modifier = Modifier.weight(1f), label = viewModel.streak, sub = "Streak", icon = Icons.Default.Whatshot, isDark = isDark)
                        MiniStat(modifier = Modifier.weight(1f), label = "Halaman ${viewModel.lastPage}", sub = "Jilid ${viewModel.lastRead}", icon = Icons.AutoMirrored.Filled.MenuBook, isDark = isDark)
                    }
                }
            }

            // --- 3. LANJUTKAN BELAJAR SECTION ---
            SectionHeader("Lanjutkan Belajar", isDark)
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onNavigateToJilid() },
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(45.dp).background(brandGreen.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = brandGreen)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Materi Jilid ${viewModel.lastRead}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textColor)
                        Text("Halaman ${viewModel.lastPage}", color = Color.Gray, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
                }
            }

            // --- 4. BIMBINGAN STATUS ---
            SectionHeader("Status Bimbingan", isDark)
            KartuStatusBimbinganModern(
                status = viewModel.bimbinganStatus,
                namaGuru = viewModel.namaGuru,
                isDark = isDark,
                onClick = { navController.navigate("santri_bimbingan_detail/$userId/$namaUser/$emailUser") }
            )

            // --- 5. MENU GRID (Identik dengan Grid Admin) ---
            SectionHeader("Layanan Yatlunah", isDark)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickMenuCard(Modifier.weight(1f), "Info Program", "Cek Detail", Icons.Default.Info, Color(0xFFFF9800), isDark, onNavigateToInfoProgram)
                QuickMenuCard(Modifier.weight(1f), "Daftar Guru", "Bimbingan", Icons.Default.School, Color(0xFF00BCC9), isDark, onNavigateToBimbingan)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// ─────────────────────────────────────────────
// Sub-Composables (UI Helper)
// ─────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, isDark: Boolean) {
    Text(
        text = title.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = if (isDark) SantriColors.textSecondary else Color.Gray,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun MiniStat(modifier: Modifier, label: String, sub: String, icon: ImageVector, isDark: Boolean) {
    Row(
        modifier = modifier
            .background(if(isDark) Color(0xFF242424) else Color(0xFFF9F9F9), RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = SantriColors.brandGreen, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if(isDark) Color.White else Color.Black)
            Text(sub, fontSize = 9.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun KartuStatusBimbinganModern(status: String, namaGuru: String, isDark: Boolean, onClick: () -> Unit) {
    val (warnaStatus, pesan) = when (status.lowercase()) {
        "aktif", "diterima" -> SantriColors.brandGreen to "Diterima oleh Ust. $namaGuru"
        "ditolak" -> Color(0xFFEF4444) to "Kuota Penuh"
        else -> Color(0xFFF59E0B) to "Menunggu Konfirmasi"
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if(isDark) SantriColors.darkSurface else Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(if(isDark) 0.dp else 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(10.dp).background(warnaStatus, CircleShape))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Bimbingan Guru", fontSize = 12.sp, color = Color.Gray)
                Text(pesan, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if(isDark) Color.White else Color.Black)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}

@Composable
private fun QuickMenuCard(modifier: Modifier, title: String, sub: String, icon: ImageVector, color: Color, isDark: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = if(isDark) SantriColors.darkSurface else Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(if(isDark) 0.dp else 2.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Box(Modifier.size(36.dp).background(color.copy(0.1f), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if(isDark) Color.White else Color.Black)
            Text(sub, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun SantriBottomBar(isDark: Boolean, brandGreen: Color, onNavigateToDashboard: () -> Unit, onNavigateToJilid: () -> Unit, onNavigateToProfile: () -> Unit) {
    NavigationBar(
        containerColor = if(isDark) Color(0xFF161616) else Color.White,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = onNavigateToDashboard,
            icon = { Icon(Icons.Default.Home, null) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = brandGreen, indicatorColor = brandGreen.copy(0.1f))
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToJilid,
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, null) },
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