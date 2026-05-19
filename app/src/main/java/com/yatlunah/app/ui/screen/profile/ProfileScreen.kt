package com.yatlunah.app.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private object ProfileColors {
    val darkBg       = Color(0xFF0F0F0F)
    val darkSurface  = Color(0xFF1A1A1A)
    val darkText1    = Color(0xFFF0F0F0)
    val darkText2    = Color(0xFFA0A0A0)

    val lightBg      = Color(0xFFF4F5F7)
    val lightSurface = Color.White
    val lightText1   = Color(0xFF1E293B)
    val lightText2   = Color(0xFF64748B)

    val brandGreen   = Color(0xFF22C55E)
    val greenDeep    = Color(0xFF065F46)
    val redDanger    = Color(0xFFEF4444)
    val blueInfo     = Color(0xFF3B82F6)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userIdAsli: String,
    namaUser: String,
    emailUser: String,
    role: String,
    viewModel: ProfileViewModel = viewModel(),
    onLogout: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToJilid: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor      = if (isDark) ProfileColors.darkBg else ProfileColors.lightBg
    val surfaceColor = if (isDark) ProfileColors.darkSurface else ProfileColors.lightSurface
    val text1        = if (isDark) ProfileColors.darkText1 else ProfileColors.lightText1
    val text2        = if (isDark) ProfileColors.darkText2 else ProfileColors.lightText2

    // States
    val currentName by viewModel.userName.collectAsState()
    val currentEmail by viewModel.userEmail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(namaUser) }

    LaunchedEffect(namaUser, emailUser) {
        viewModel.setUserData(namaUser, emailUser)
        tempName = namaUser
    }

    val roleLabel = when (role.lowercase()) {
        "admin" -> "Administrator Pusat"
        "guru"  -> "Guru Pembimbing"
        "mitra", "admin_mitra", "adminmitra" -> "Pengurus Lembaga / Mitra"
        else    -> "Peserta Yatlunah"
    }

    // UX Edit Nama Pop-up
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Ubah Nama Profil", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Nama Baru") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.updateProfileName(userIdAsli, tempName) { showEditNameDialog = false } },
                    colors = ButtonDefaults.buttonColors(containerColor = ProfileColors.brandGreen)
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                    else Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) { Text("Batal", color = text2) }
            },
            containerColor = surfaceColor
        )
    }

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            ProfileBottomBar(
                isDark = isDark,
                brandGreen = ProfileColors.brandGreen,
                role = role,
                onNavigateToHome = onNavigateToHome,
                onNavigateToJilid = onNavigateToJilid
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── 1. HEADER (Gradient & Melengkung) ──────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Brush.verticalGradient(listOf(ProfileColors.greenDeep, ProfileColors.brandGreen)))
                    .padding(top = 40.dp, bottom = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Foto Profil
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .padding(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = CircleShape,
                            color = Color.White
                        ) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.padding(16.dp), tint = ProfileColors.brandGreen)
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    // Identitas
                    Text(text = currentName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(text = roleLabel, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── 2. INFORMASI AKUN ─────────────────────────────────────────
            SectionTitle("Informasi Akun", text1)
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).shadow(2.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column {
                    SettingsRow(icon = Icons.Default.Email, iconTint = ProfileColors.blueInfo, title = "Email Terdaftar", subtitle = currentEmail, isDark = isDark)
                    HorizontalDivider(color = if(isDark) Color(0xFF2A2A2A) else Color(0xFFF1F5F9))
                    SettingsRow(
                        icon = Icons.Default.Edit,
                        iconTint = ProfileColors.brandGreen,
                        title = "Ubah Nama Profil",
                        isDark = isDark,
                        showArrow = true,
                        onClick = { showEditNameDialog = true }
                    )
                    HorizontalDivider(color = if(isDark) Color(0xFF2A2A2A) else Color(0xFFF1F5F9))
                    SettingsRow(
                        icon = Icons.Default.Lock,
                        iconTint = Color(0xFFF59E0B),
                        title = "Ganti Password",
                        isDark = isDark,
                        showArrow = true,
                        onClick = { /* TODO: Navigasi ubah password */ }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── 3. PENGATURAN & BANTUAN (SARAN FITUR TAMBAHAN) ─────────────
            SectionTitle("Pengaturan & Dukungan", text1)
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).shadow(2.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.NotificationsActive,
                        iconTint = ProfileColors.brandGreen,
                        title = "Pengaturan Notifikasi",
                        isDark = isDark,
                        showArrow = true
                    )
                    HorizontalDivider(color = if(isDark) Color(0xFF2A2A2A) else Color(0xFFF1F5F9))
                    SettingsRow(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        iconTint = ProfileColors.blueInfo,
                        title = "Pusat Bantuan",
                        isDark = isDark,
                        showArrow = true
                    )
                    HorizontalDivider(color = if(isDark) Color(0xFF2A2A2A) else Color(0xFFF1F5F9))
                    SettingsRow(
                        icon = Icons.Default.PrivacyTip,
                        iconTint = text2,
                        title = "Kebijakan Privasi",
                        isDark = isDark,
                        showArrow = true
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── 4. TOMBOL LOGOUT ───────────────────────────────────────────
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ProfileColors.redDanger.copy(alpha = 0.1f))
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null, tint = ProfileColors.redDanger, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Keluar Akun", color = ProfileColors.redDanger, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(Modifier.height(32.dp)) // Ruang ekstra di bawah
        }
    }
}

// ====================================================================
// COMPOSABLE UI COMPONENTS
// ====================================================================

@Composable
private fun SectionTitle(title: String, textColor: Color) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = textColor,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String? = null,
    isDark: Boolean,
    showArrow: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(iconTint.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if(isDark) Color.White else Color(0xFF1E293B))
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = if(isDark) Color(0xFFA0A0A0) else Color(0xFF64748B))
            }
        }
        if (showArrow) {
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, tint = if(isDark) Color(0xFF505050) else Color(0xFFCBD5E1), modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun ProfileBottomBar(
    isDark: Boolean,
    brandGreen: Color,
    role: String,
    onNavigateToHome: () -> Unit,
    onNavigateToJilid: () -> Unit
) {
    val inactiveColor = if (isDark) Color(0xFF606060) else Color.Gray
    val barBg = if (isDark) Color(0xFF161616) else Color.White

    NavigationBar(
        containerColor = barBg,
        tonalElevation = if (isDark) 0.dp else 8.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Default.Home, null) },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = inactiveColor, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToJilid,
            icon = {
                val icon = when(role.lowercase()) {
                    "admin", "mitra", "admin_mitra", "adminmitra" -> Icons.AutoMirrored.Filled.List
                    "guru"  -> Icons.Default.FactCheck
                    else    -> Icons.AutoMirrored.Filled.MenuBook
                }
                Icon(icon, null)
            },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = inactiveColor, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Default.Person, null, modifier = Modifier.size(28.dp)) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = brandGreen, indicatorColor = if (isDark) Color(0x1422C55E) else Color(0xFFE8FFF0))
        )
    }
}