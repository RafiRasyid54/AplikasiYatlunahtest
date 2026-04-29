package com.yatlunah.app.ui.screen.santri

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import com.yatlunah.app.R

private object SantriColors {
    val brandGreen      = Color(0xFF22C55E)
    val lightGreenBg    = Color(0xFFF0FDF4)
    val softOrange      = Color(0xFFFFF7ED)
    val darkGreenCard   = Color(0xFF065F46)
    val textSecondary   = Color(0xFF6B7280)
    val darkBackground  = Color(0xFF0F0F0F) // Dari biru dongker ke Hitam/Grey Gelap
    val darkSurface     = Color(0xFF1A1A1A) // Dari biru dongker ke Grey Permukaan
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SantriDashboardScreen(
    userId: String,
    namaUser: String,
    navController: NavController,
    onNavigateToJilid: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToBimbingan: () -> Unit,
    onNavigateToInfoProgram: () -> Unit,
    viewModel: SantriViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val isGuest = userId == "guest_user"

    val bgColor = if (isDark) SantriColors.darkBackground else SantriColors.lightGreenBg
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)

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
        viewModel.fetchQuoteBerdasarkanHari()
        if (!isGuest) {
            viewModel.fetchStats(userId)
            viewModel.fetchStatusBimbingan(userId)
        }
    }

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            // NAVBAR SESUAI PROGRAM ASLI
            SantriBottomBar(
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Halo, Sahabat Cilik!",
                        fontSize = 14.sp,
                        color = SantriColors.brandGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = namaUser,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor
                    )
                }
                Image(
                    painter            = painterResource(id = R.drawable.yatlunahlogo),
                    contentDescription = "Logo Yatlunah",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            WaktuShalatBanner(
                hijriDate = viewModel.hijriDate,
                lokasi = viewModel.currentLocationName,
                sholatNama = viewModel.currentShalatName,
                sholatWaktu = viewModel.currentShalatTime
            )

            // Pastikan memberikan dua parameter: quote DAN source
            KontenDinamisSlider(
                quote = viewModel.currentQuote,
                source = viewModel.currentSource
            )

            if (!isGuest && viewModel.bimbinganStatus.isNotEmpty()) {
                StatusBimbinganCard(
                    status = viewModel.bimbinganStatus,
                    guru = viewModel.namaGuru,
                    isDark = isDark,
                    onClick = onNavigateToBimbingan
                )
            }

            SectionHeader("Petualangan Belajar", isDark)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickMenuCard(
                    modifier = Modifier.weight(1f),
                    title = "Program",
                    sub = "Yuk Lihat Kelasmu",
                    icon = Icons.Default.AutoAwesome,
                    color = Color(0xFFF59E0B),
                    isDark = isDark,
                    onClick = onNavigateToInfoProgram
                )
                QuickMenuCard(
                    modifier = Modifier.weight(1f),
                    title = "Bimbingan",
                    sub = "Ketemu Guru",
                    icon = Icons.Default.Face,
                    color = Color(0xFF10B981),
                    isDark = isDark,
                    onClick = onNavigateToBimbingan
                )
            }

            if (isGuest) {
                GuestWelcomeCard(SantriColors.brandGreen, isDark, onNavigateToProfile)
            } else {
                ProgressSection(viewModel, textColor, SantriColors.brandGreen, isDark, onNavigateToJilid)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Komponen UI Pendukung Tetap Ceria
@Composable
private fun WaktuShalatBanner(hijriDate: String, lokasi: String, sholatNama: String, sholatWaktu: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(listOf(SantriColors.darkGreenCard, Color(0xFF064E3B))))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(hijriDate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(12.dp), tint = Color.LightGray)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KontenDinamisSlider(quote: String, source: String) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    Column {
        HorizontalPager(state = pagerState, modifier = Modifier.height(150.dp)) { page ->
            // Menentukan konten tiap slide
            val (title, content, quoteSource, icon, color) = when (page) {
                0 -> Quintuple("Inspirasi Hari Ini", quote, source, Icons.Default.Stars, Color(0xFF8B5CF6))
                1 -> Quintuple("Ayat Pilihan", "Berlomba-lombalah dalam kebaikan.", "QS. Al-Baqarah: 148", Icons.AutoMirrored.Filled.MenuBook, Color(0xFFEC4899))
                else -> Quintuple("Tips Santri", "Adab Membaca Al-Qur'an", "Buku Adab", Icons.AutoMirrored.Filled.Article, Color(0xFF06B6D4))
            }

            Card(
                modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = color)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Text(
                            text = "\"$content\"",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp),
                            maxLines = 3
                        )
                    }

                    // Sumber ditampilkan di sini
                    Text(
                        text = "— $quoteSource",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Dots Indicator
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), Arrangement.Center) {
            repeat(3) { i ->
                val active = pagerState.currentPage == i
                Box(
                    Modifier
                        .padding(2.dp)
                        .size(if(active) 12.dp else 6.dp, 6.dp)
                        .clip(CircleShape)
                        .background(if(active) SantriColors.brandGreen else Color.LightGray)
                )
            }
        }
    }
}

@Composable
private fun QuickMenuCard(modifier: Modifier, title: String, sub: String, icon: ImageVector, color: Color, isDark: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.shadow(8.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = if(isDark) SantriColors.darkSurface else Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if(isDark) Color.White else Color.Black)
            Text(sub, fontSize = 11.sp, color = SantriColors.textSecondary)
        }
    }
}

@Composable
private fun ProgressSection(viewModel: SantriViewModel, textColor: Color, brandGreen: Color, isDark: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = if(isDark) SantriColors.darkSurface else Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Semangat Belajarmu! ✨", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { viewModel.progressPercent },
                modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                color = brandGreen,
                trackColor = brandGreen.copy(0.2f)
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniStat(Modifier.weight(1f), "${viewModel.streak} Hari", "Rajin!", Icons.Default.Whatshot, isDark)
                MiniStat(Modifier.weight(1f), "Hal. ${viewModel.lastPage}", viewModel.lastRead, Icons.AutoMirrored.Filled.MenuBook, isDark)
            }
        }
    }
}

@Composable
private fun MiniStat(modifier: Modifier, label: String, sub: String, icon: ImageVector, isDark: Boolean) {
    Row(
        modifier = modifier.background(if(isDark) Color(0xFF334155) else Color(0xFFF8FAFC), RoundedCornerShape(16.dp)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = SantriColors.brandGreen, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if(isDark) Color.White else Color.Black)
            Text(sub, fontSize = 10.sp, color = SantriColors.textSecondary)
        }
    }
}

@Composable
private fun StatusBimbinganCard(status: String, guru: String, isDark: Boolean, onClick: () -> Unit) {
    val statusColor = if (status == "Aktif") Color(0xFF22C55E) else Color(0xFFF59E0B)
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.15f)),
        border = BorderStroke(1.dp, statusColor.copy(0.3f))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(statusColor))
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Bimbingan $status", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if(isDark) Color.White else Color.Black)
                Text("Pendaftar: $guru", fontSize = 12.sp, color = SantriColors.textSecondary)
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, modifier = Modifier.size(14.dp), tint = SantriColors.textSecondary)
        }
    }
}

@Composable
private fun GuestWelcomeCard(brandGreen: Color, isDark: Boolean, onRegister: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if(isDark) SantriColors.darkSurface else SantriColors.softOrange),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Ayo Bergabung! 🚀", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Simpan progres belajarmu dan dapatkan lencana keren!", fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 8.dp))
            Button(onClick = onRegister, colors = ButtonDefaults.buttonColors(containerColor = brandGreen), shape = RoundedCornerShape(12.dp)) {
                Text("Daftar Sekarang", fontWeight = FontWeight.Bold)
            }
        }
    }
}

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

// --- NAVBAR SESUAI KODE ASLI ---
@Composable
private fun SantriBottomBar(onNavigateToDashboard: () -> Unit, onNavigateToJilid: () -> Unit, onNavigateToProfile: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val brandGreen = SantriColors.brandGreen
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

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)// Letakkan di akhir file (paling bawah)
data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)