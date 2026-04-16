package com.yatlunah.app.ui.screen.guru

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatlunah.app.data.model.Bimbingan

// ─────────────────────────────────────────────
// Token Warna Identik
// ─────────────────────────────────────────────
private object SantriAktifColors {
    val brandGreen      = Color(0xFF00D639)
    val whatsappGreen   = Color(0xFF25D366)
    val darkBackground  = Color(0xFF0F0F0F)
    val darkSurface     = Color(0xFF1A1A1A)
    val lightBackground = Color(0xFFF4F5F7)
    val textSecondary   = Color(0xFFA0A0A0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruSantriAktifScreen(
    idGuru: String,
    onBack: () -> Unit,
    daftarSantri: List<Bimbingan> = listOf()
) {
    val isDark = isSystemInDarkTheme()

    // Theme Logic
    val bgColor = if (isDark) SantriAktifColors.darkBackground else SantriAktifColors.lightBackground
    val surfaceColor = if (isDark) SantriAktifColors.darkSurface else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Santri Bimbingan",
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (daftarSantri.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Belum ada santri bimbingan aktif.",
                        color = SantriAktifColors.textSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "DAFTAR SANTRI AKTIF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) SantriAktifColors.textSecondary else Color.Gray,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(daftarSantri) { santri ->
                        ItemSantriAktifCard(
                            santri = santri,
                            surfaceColor = surfaceColor,
                            isDark = isDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemSantriAktifCard(
    santri: Bimbingan,
    surfaceColor: Color,
    isDark: Boolean
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar Placeholder
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = SantriAktifColors.brandGreen.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = SantriAktifColors.brandGreen,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Santri #${santri.userId}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isDark) Color.White else Color.Black
                    )
                    Text(
                        text = santri.jenisBimbingan,
                        color = SantriAktifColors.brandGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = if (isDark) Color(0xFF2E2E2E) else Color(0xFFF0F0F0),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Terdaftar Sejak",
                        fontSize = 10.sp,
                        color = SantriAktifColors.textSecondary
                    )
                    Text(
                        text = santri.tanggalDaftar?.take(10) ?: "-",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color.LightGray else Color.DarkGray
                    )
                }

                Button(
                    onClick = {
                        val url = "https://api.whatsapp.com/send?phone=6281234567890&text=Assalamu'alaikum, ini Ustadz pembimbing dari aplikasi Yatlunah..."
                        val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SantriAktifColors.whatsappGreen),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Whatsapp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Hubungi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}