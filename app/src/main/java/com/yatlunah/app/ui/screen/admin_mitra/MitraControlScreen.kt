package com.yatlunah.app.ui.screen.admin_mitra

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitraControlScreen(
    onNavigateToUserList: (String) -> Unit,
    onBack: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF0F0F0F) else Color(0xFFF4F5F7)
    val cardColor = if (isDark) Color(0xFF1A1A1A) else Color.White

    Scaffold(
        containerColor = bgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kontrol Lembaga", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = bgColor)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Pilih kategori manajemen:",
                fontSize = 14.sp,
                color = Color.Gray
            )

            MitraControlCard(
                label = "Manajemen Guru",
                icon = Icons.Default.PersonSearch,
                color = Color(0xFF3B82F6),
                containerColor = cardColor,
                onClick = { onNavigateToUserList("guru") }
            )

            MitraControlCard(
                label = "Manajemen Santri",
                icon = Icons.Default.People,
                color = Color(0xFF00D639),
                containerColor = cardColor,
                onClick = { onNavigateToUserList("santri") }
            )
        }
    }
}

@Composable
private fun MitraControlCard(
    label: String,
    icon: ImageVector,
    color: Color,
    containerColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(Modifier.width(16.dp))
            Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}