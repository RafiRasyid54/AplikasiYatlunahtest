package com.yatlunah.app.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation // ✅ WAJIB IMPORT INI
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.ui.screen.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userIdAsli: String,
    namaUser: String,
    emailUser: String,
    viewModel: ProfileViewModel = viewModel(),
    onLogout: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToJilid: () -> Unit
) {
    val brightGreen = Color(0xFF00D639)
    val lightGrayBg = Color(0xFFF4F5F7)

    // States Lokal
    var isEditingName by remember { mutableStateOf(false) }
    var showPasswordSheet by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(namaUser) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    // ✅ STATE BARU UNTUK KONTROL MATA
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }

    // State dari ViewModel
    val currentName by viewModel.userName.collectAsState()
    val currentEmail by viewModel.userEmail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(namaUser, emailUser) {
        viewModel.setUserData(namaUser, emailUser)
        tempName = namaUser
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
                tonalElevation = 8.dp
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    IconButton(onClick = onNavigateToHome) { Icon(Icons.Default.Home, null, tint = Color.Gray) }
                    IconButton(onClick = onNavigateToJilid) { Icon(Icons.AutoMirrored.Filled.List, null, tint = Color.Gray) }
                    IconButton(onClick = {}) { Icon(Icons.Default.Person, null, tint = brightGreen, modifier = Modifier.size(30.dp)) }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().background(lightGrayBg).padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            Box(modifier = Modifier.fillMaxWidth().height(180.dp).background(brightGreen), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(modifier = Modifier.size(90.dp), shape = CircleShape, color = Color.White) {
                        Icon(Icons.Default.Person, null, modifier = Modifier.padding(20.dp), tint = brightGreen)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Peserta Yatlunah", color = Color.White, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- INFO CARD ---
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Detail Profil", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isEditingName) {
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            label = { Text("Nama Baru") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = brightGreen)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.updateProfileName(userIdAsli, tempName) { isEditingName = false }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = brightGreen),
                            enabled = !isLoading
                        ) {
                            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            else Text("Simpan Nama")
                        }
                    } else {
                        ProfileItem(Icons.Default.Badge, "Nama", currentName)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.LightGray)

                        ProfileItem(Icons.Default.Email, "Email", currentEmail)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { isEditingName = true }) {
                                Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                                Text(" Ubah Nama", color = brightGreen)
                            }
                            TextButton(onClick = { showPasswordSheet = true }) {
                                Icon(Icons.Default.Lock, null, modifier = Modifier.size(16.dp))
                                Text(" Ganti Password", color = Color.Gray)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(2.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.Red)
                Text(" Keluar Akun", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }

        // --- PASSWORD SHEET ---
        if (showPasswordSheet) {
            ModalBottomSheet(onDismissRequest = {
                showPasswordSheet = false
                oldPasswordVisible = false // Reset mata saat tutup
                newPasswordVisible = false
            }, containerColor = Color.White) {
                Column(modifier = Modifier.padding(24.dp).padding(bottom = 32.dp)) {
                    Text("Ganti Kata Sandi", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(20.dp))

                    // --- PASSWORD LAMA ---
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Password Lama") },
                        modifier = Modifier.fillMaxWidth(),
                        // ✅ PERUBAHAN DISINI: Cek variabel visible
                        visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (oldPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                                Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                            }
                        },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // --- PASSWORD BARU ---
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Password Baru") },
                        modifier = Modifier.fillMaxWidth(),
                        // ✅ PERUBAHAN DISINI: Cek variabel visible
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                            }
                        },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.updatePassword(userIdAsli, oldPassword, newPassword) {
                                showPasswordSheet = false
                                oldPassword = ""
                                newPassword = ""
                                oldPasswordVisible = false
                                newPasswordVisible = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = brightGreen),
                        enabled = !isLoading && newPassword.isNotEmpty() && oldPassword.isNotEmpty()
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("Update Password")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}