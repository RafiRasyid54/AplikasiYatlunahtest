package com.yatlunah.app.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private object ProfileColors {
    val darkBg       = Color(0xFF0F0F0F)
    val darkSurface  = Color(0xFF1A1A1A)
    val darkSurface2 = Color(0xFF242424)
    val darkBorder   = Color(0x12FFFFFF)
    val darkText1    = Color(0xFFF0F0F0)
    val darkText2    = Color(0xFFA0A0A0)
    val darkText3    = Color(0xFF606060)
    val darkGreen    = Color(0xFF22C55E)
    val darkGreenBg  = Color(0xFF14532D)
    val darkGreenTint= Color(0x1422C55E)
    val darkRedTint  = Color(0x14DC2626)
    val darkRedText  = Color(0xFFFCA5A5)

    val lightBg      = Color(0xFFF4F5F7)
    val lightSurface = Color.White
    val lightBorder  = Color(0xFFE5E5E5)
    val lightText2   = Color(0xFF888888)
    val lightText3   = Color(0xFFAAAAAA)
    val lightGreen   = Color(0xFF00C132)
    val lightGreenBg = Color(0xFF00D639)
    val lightGreenTint = Color(0xFFF0FDF4)
    val lightRedTint = Color(0xFFFFF5F5)
    val lightRedText = Color(0xFFDC2626)
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
    val bgColor       = if (isDark) ProfileColors.darkBg       else ProfileColors.lightBg
    val surfaceColor  = if (isDark) ProfileColors.darkSurface  else ProfileColors.lightSurface
    val surface2Color = if (isDark) ProfileColors.darkSurface2 else ProfileColors.lightBg
    val borderColor   = if (isDark) ProfileColors.darkBorder   else ProfileColors.lightBorder
    val text1         = if (isDark) ProfileColors.darkText1    else Color(0xFF111111)
    val text2         = if (isDark) ProfileColors.darkText2    else ProfileColors.lightText2
    val text3         = if (isDark) ProfileColors.darkText3    else ProfileColors.lightText3
    val brandGreen    = if (isDark) ProfileColors.darkGreen    else ProfileColors.lightGreen
    val headerBg      = if (isDark) ProfileColors.darkGreenBg  else ProfileColors.lightGreenBg

    var isEditingName by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(namaUser) }

    val currentName by viewModel.userName.collectAsState()
    val currentEmail by viewModel.userEmail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(namaUser, emailUser) {
        viewModel.setUserData(namaUser, emailUser)
        tempName = namaUser
    }

    val roleLabel = when (role.lowercase()) {
        "admin" -> "Administrator"
        "guru"  -> "Guru Pembimbing"
        "adminmitra" -> "Admin Lembaga / Mitra"
        else    -> "Peserta Yatlunah"
    }

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            ProfileBottomBar(
                isDark = isDark,
                brandGreen = brandGreen,
                role = role,
                onNavigateToHome = onNavigateToHome,
                onNavigateToJilid = onNavigateToJilid
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(170.dp).background(headerBg),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = if (isDark) ProfileColors.darkGreenTint else Color.White
                    ) {
                        Icon(Icons.Default.Person, null, modifier = Modifier.padding(18.dp), tint = brandGreen)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(text = roleLabel, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 1.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("Detail Profil", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = text1)
                    Spacer(Modifier.height(14.dp))

                    if (isEditingName) {
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            label = { Text("Nama Baru") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = brandGreen, focusedLabelColor = brandGreen)
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { isEditingName = false }, modifier = Modifier.weight(1f)) { Text("Batal", color = text2) }
                            Button(
                                onClick = { viewModel.updateProfileName(userIdAsli, tempName) { isEditingName = false } },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
                            ) {
                                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                                else Text("Simpan", color = Color.White)
                            }
                        }
                    } else {
                        // ✅ SEKARANG SUDAH ADA REFERENSINYA
                        ProfileInfoRow(Icons.Default.Badge, surface2Color, brandGreen, "Nama", currentName, text3, text1, borderColor, true)
                        ProfileInfoRow(Icons.Default.Email, surface2Color, if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB), "Email", currentEmail, text3, text1, borderColor, false)

                        Spacer(Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { isEditingName = true }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Ubah Nama", fontSize = 12.sp)
                            }
                            OutlinedButton(onClick = { /* Password Logic */ }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Lock, null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Password", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isDark) ProfileColors.darkRedTint else ProfileColors.lightRedTint,
                    contentColor = if (isDark) ProfileColors.darkRedText else ProfileColors.lightRedText
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Keluar Akun", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ✅ FUNGSI COMPOSABLE PEMBANTU (Wajib ada di dalam file yang sama)
@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color,
    dividerColor: Color,
    showDivider: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(36.dp).background(iconBg, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(label, fontSize = 11.sp, color = labelColor)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
        }
    }
    if (showDivider) HorizontalDivider(color = dividerColor, thickness = 0.5.dp)
}

@Composable
private fun ProfileBottomBar(
    isDark: Boolean,
    brandGreen: Color,
    role: String,
    onNavigateToHome: () -> Unit,
    onNavigateToJilid: () -> Unit
) {
    val inactiveColor = if (isDark) Color(0xFF505050) else Color.Gray
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
                    "admin", "adminmitra" -> Icons.AutoMirrored.Filled.List
                    "guru"  -> Icons.Default.FactCheck // ✅ Ikon untuk guru
                    else    -> Icons.AutoMirrored.Filled.MenuBook // ✅ Menggunakan AutoMirrored
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