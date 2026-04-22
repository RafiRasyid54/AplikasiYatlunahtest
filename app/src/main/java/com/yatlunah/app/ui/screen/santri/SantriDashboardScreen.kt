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
import androidx.compose.material.icons.automirrored.filled.Article
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.yatlunah.app.R
import com.yatlunah.app.utils.LocationHelper // Pastikan file ini sudah dibuat
import java.util.*

private object SantriColors {
    val brandGreen     = Color(0xFF00D639)
    val darkGreenDeep  = Color(0xFF14532D)
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
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val isGuest = userId == "guest_user"

    val bgColor = if (isDark) SantriColors.darkBackground else SantriColors.lightBackground
    val surfaceColor = if (isDark) SantriColors.darkSurface else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)
    val brandGreen = SantriColors.brandGreen

    // State untuk nama lokasi real-time
    var currentCity by remember { mutableStateOf("Mencari Lokasi...") }
    val locationHelper = remember { LocationHelper(context) }

    // Launcher untuk Izin Lokasi
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            locationHelper.getCurrentLocation { lat, lon ->
                viewModel.fetchPrayerAndHijri(lat, lon)
                currentCity = "Lokasi Terdeteksi" // Bisa dikembangkan dengan Geocoder
            }
        } else {
            currentCity = "Izin Lokasi Ditolak"
            // Fallback ke Bandung jika izin ditolak
            viewModel.fetchPrayerAndHijri(-6.9175, 107.6191)
        }
    }

    LaunchedEffect(userId) {
        if (!isGuest) {
            viewModel.fetchStats(userId)
            viewModel.fetchStatusBimbingan(userId)
        }

        // Minta izin lokasi saat aplikasi dibuka
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        viewModel.fetchQuoteBerdasarkanHari()
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
            // 1. Info Waktu & Lokasi (Dinamis)
            InfoWaktuLokasiSection(
                isDark = isDark,
                hijriDate = viewModel.hijriDate,
                lokasi = currentCity,
                prayerTimes = viewModel.prayerTimes
            )

            // 2. Header Greeting
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isGuest) "Ahlan wa Sahlan," else "Selamat Datang,",
                        fontSize = 14.sp,
                        color = if(isDark) SantriColors.textSecondary else Color.Gray
                    )
                    Text(namaUser, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
                }
                Icon(
                    painter = painterResource(id = R.drawable.logo_yatlunah),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)),
                    tint = brandGreen
                )
            }

            // 3. Slider Konten Dinamis
            KontenDinamisSlider(isDark, viewModel.currentQuote)

            // 4. Menu Belajar Cepat
            SectionHeader("Menu Belajar Cepat", isDark)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GameMenuCard(Modifier.weight(1f), "Mengenal Hijaiyyah", Icons.Default.Grid4x4, Color(0xFF4CAF50), isDark, onNavigateToJilid)
                GameMenuCard(Modifier.weight(1f), "Latihan", Icons.Default.Extension, Color(0xFFFF9800), isDark, { /* Nav ke Latihan */ })
            }

            // 5. Progress Section
            if (isGuest) {
                GuestWelcomeCard(brandGreen, surfaceColor, textColor, onNavigateToProfile)
            } else {
                ProgressSection(viewModel, surfaceColor, textColor, brandGreen, isDark, onNavigateToJilid)
            }

            // 6. Kemitraan
            SectionHeader("Kemitraan", isDark)
            QuickMenuCard(
                modifier = Modifier.fillMaxWidth(),
                title = "Menjadi Mitra Lembaga",
                sub = "Daftarkan Masjid/Lembaga Anda",
                icon = Icons.Default.Handshake,
                color = Color(0xFF2196F3),
                isDark = isDark,
                onClick = { /* Nav ke Mitra */ }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Kontak bantuan: $emailUser",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun InfoWaktuLokasiSection(
    isDark: Boolean,
    hijriDate: String,
    lokasi: String,
    prayerTimes: Map<String, String>
) {
    // Logika menampilkan jadwal sholat terdekat
    val sholatNama = "Shubuh"
    val sholatWaktu = prayerTimes["Fajr"] ?: "--:--"

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = hijriDate.ifEmpty { "Memuat tanggal..." },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SantriColors.brandGreen
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text(lokasi, fontSize = 11.sp, color = Color.Gray)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(sholatNama, fontSize = 11.sp, color = Color.Gray)
            Text(
                text = sholatWaktu,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if(isDark) Color.White else Color.Black
            )
        }
    }
}

@Composable
private fun KontenDinamisSlider(isDark: Boolean, quote: String) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    Column {
        HorizontalPager(state = pagerState) { page ->
            val (title, content, icon) = when(page) {
                0 -> Triple("Quotes Harian", quote, Icons.Default.FormatQuote)
                1 -> Triple("Ayat Pilihan", "Al-Baqarah: 183", Icons.AutoMirrored.Filled.MenuBook)
                else -> Triple("Artikel Islami", "Adab Membaca Al-Qur'an", Icons.AutoMirrored.Filled.Article)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(SantriColors.darkGreenDeep, SantriColors.darkGreenDim)),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, null, tint = Color.White.copy(0.7f), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(content, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.Center) {
            repeat(3) { iteration ->
                val color = if (pagerState.currentPage == iteration) SantriColors.brandGreen else Color.LightGray
                Box(Modifier.padding(2.dp).clip(CircleShape).background(color).size(6.dp))
            }
        }
    }
}

@Composable
private fun ProgressSection(
    viewModel: SantriViewModel,
    surfaceColor: Color,
    textColor: Color,
    brandGreen: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick() },
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
                MiniStat(Modifier.weight(1f), viewModel.streak, "Streak", Icons.Default.Whatshot, isDark)
                MiniStat(Modifier.weight(1f), viewModel.lastPage, viewModel.lastRead, Icons.AutoMirrored.Filled.MenuBook, isDark)
            }
        }
    }
}

@Composable
private fun GuestWelcomeCard(brandGreen: Color, surfaceColor: Color, textColor: Color, onRegister: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, brandGreen.copy(0.3f))
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Mode Tamu Aktif", fontWeight = FontWeight.Bold, color = textColor)
            Text(
                text = "Daftar sekarang untuk menyimpan progres belajar dan bimbingan bersama Guru.",
                fontSize = 12.sp, textAlign = TextAlign.Center, color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Button(
                onClick = onRegister,
                colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Daftar Sekarang", color = Color.White)
            }
        }
    }
}

@Composable
private fun GameMenuCard(modifier: Modifier, title: String, icon: ImageVector, color: Color, isDark: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = if(isDark) SantriColors.darkSurface else Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = if(isDark) Color.White else Color.Black, textAlign = TextAlign.Center)
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
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if(isDark) Color.White else Color.Black)
            Text(sub, fontSize = 11.sp, color = Color.Gray)
        }
    }
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
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if(isDark) Color.White else Color.Black)
            Text(sub, fontSize = 9.sp, color = Color.Gray)
        }
    }
}

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