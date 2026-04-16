package com.yatlunah.app.ui.screen.guru

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatlunah.app.R

// ─────────────────────────────────────────────
// Token Warna Unik
// ─────────────────────────────────────────────
private object GuruJilidColors {
    val brandGreen     = Color(0xFF00D639)
    val darkBackground = Color(0xFF0F0F0F)
    val darkSurface    = Color(0xFF1A1A1A)
    val lightBackground = Color(0xFFF4F5F7)
    val textSecondary  = Color(0xFFA0A0A0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruJilidMenuScreen(
    onBack: () -> Unit,
    onNavigateToQueue: (Int) -> Unit
) {
    val isDark = isSystemInDarkTheme()

    // Inisialisasi Tema
    val bgColor = if (isDark) GuruJilidColors.darkBackground else GuruJilidColors.lightBackground
    val surfaceColor = if (isDark) GuruJilidColors.darkSurface else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)
    val brandGreen = GuruJilidColors.brandGreen

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Validasi Setoran",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textColor
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = textColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = surfaceColor
                    )
                )
                HorizontalDivider(
                    color = if (isDark) Color(0xFF2E2E2E) else Color(0xFFE5E5E5),
                    thickness = 0.5.dp
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // --- HEADER SECTION ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pilih Jilid",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Kelola antrean setoran santri Anda",
                        color = GuruJilidColors.textSecondary,
                        fontSize = 14.sp
                    )
                }

                // Logo dengan Efek Glassmorphism Ringan di Mode Gelap
                Surface(
                    modifier = Modifier.size(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.Transparent
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_yatlunah),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }

            Text(
                text = "MODUL PEMBELAJARAN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) GuruJilidColors.textSecondary else Color.Gray,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // --- GRID JILID ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items((1..6).toList()) { nomor ->
                    JilidGridCard(
                        nomor = nomor,
                        surfaceColor = surfaceColor,
                        brandGreen = brandGreen,
                        isDark = isDark,
                        onClick = { onNavigateToQueue(nomor) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JilidGridCard(
    nomor: Int,
    surfaceColor: Color,
    brandGreen: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon Container
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = brandGreen.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = brandGreen,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Jilid $nomor",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (isDark) Color.White else Color.Black
            )

            Text(
                text = "Cek Antrean",
                fontSize = 12.sp,
                color = GuruJilidColors.textSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}