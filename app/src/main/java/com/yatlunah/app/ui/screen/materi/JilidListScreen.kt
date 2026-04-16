package com.yatlunah.app.ui.screen.materi

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.data.model.JilidData
import com.yatlunah.app.ui.screen.materi.JilidViewModel

// ─────────────────────────────────────────────
// Token Warna Konsisten
// ─────────────────────────────────────────────
private object JilidColors {
    val brandGreen     = Color(0xFF00D639)
    val darkBackground = Color(0xFF0F0F0F)
    val darkSurface    = Color(0xFF1A1A1A)
    val lightBackground = Color(0xFFF4F5F7)
    val textSecondary  = Color(0xFFA0A0A0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JilidListScreen(
    viewModel: JilidViewModel = viewModel(),
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToHome: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) JilidColors.darkBackground else JilidColors.lightBackground
    val surfaceColor = if (isDark) JilidColors.darkSurface else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)
    val brandGreen = JilidColors.brandGreen

    val listJilid by viewModel.jilidList.collectAsState()

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Daftar Jilid Iqra",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textColor
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateToHome) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "MATERI PEMBELAJARAN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) JilidColors.textSecondary else Color.Gray,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            items(listJilid) { jilid ->
                JilidCard(
                    jilid = jilid,
                    surfaceColor = surfaceColor,
                    brandGreen = brandGreen,
                    isDark = isDark,
                    onClick = { onNavigateToDetail(jilid.nomorJilid) },
                    onDownloadClick = {
                        if (!jilid.isDownloaded) {
                            viewModel.downloadJilid(jilid)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JilidCard(
    jilid: JilidData,
    surfaceColor: Color,
    brandGreen: Color,
    isDark: Boolean,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nomor Jilid Box
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(brandGreen.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${jilid.nomorJilid}",
                    color = brandGreen,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = jilid.judulJilid,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isDark) Color.White else Color.Black
                )
                Text(
                    text = "Ukuran: ${jilid.fileSize}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                // Download Progress
                if (jilid.downloadProgress > 0f && jilid.downloadProgress < 1f) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { jilid.downloadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = brandGreen,
                        trackColor = if (isDark) Color(0xFF2E2E2E) else Color(0xFFEEEEEE)
                    )
                }
            }

            // Download/Success Icon
            IconButton(
                onClick = onDownloadClick,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = if (jilid.isDownloaded) brandGreen else Color.Gray
                )
            ) {
                Icon(
                    imageVector = if (jilid.isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}