package com.yatlunah.app.ui.screen.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.R

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {},
    onRegisterSucces: () -> Unit = {} // Typo dipertahankan sesuai MainActivity
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // LOGIKA NAVIGASI OTOMATIS
    LaunchedEffect(viewModel.registerStatus) {
        if (viewModel.registerStatus.contains("berhasil", ignoreCase = true)) {
            onRegisterSucces()
        }
    }

    val brightGreen = Color(0xFF00D639)
    val inputIconColor = Color(0xFF2B2B43)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // ✅ Dukungan Dark Mode
    ) {
        // --- Bagian Logo ---
        Box(
            modifier = Modifier.fillMaxWidth().weight(0.35f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_yatlunah),
                contentDescription = "Logo Yatlunah",
                modifier = Modifier.size(160.dp)
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.65f)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
            color = MaterialTheme.colorScheme.background // ✅ Disamakan dengan LoginScreen
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SIGNUP",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground // ✅ Dukungan Dark Mode
                )
                Spacer(modifier = Modifier.height(24.dp))

                // --- Form Input ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // ✅ Dukungan Dark Mode
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // 1. Kolom Username
                        TextField(
                            value = username,
                            onValueChange = { username = it },
                            placeholder = { Text("Username", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = inputIconColor) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,   // ✅ Teks menyesuaikan tema
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface, // ✅ Teks menyesuaikan tema
                                cursorColor = brightGreen,        // ✅ Kursor selaras dengan brand
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.LightGray,
                                unfocusedIndicatorColor = Color.LightGray
                            ),
                            singleLine = true
                        )

                        // 2. Kolom Email
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("Email", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = inputIconColor) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = brightGreen,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.LightGray,
                                unfocusedIndicatorColor = Color.LightGray
                            ),
                            singleLine = true
                        )

                        // 3. Kolom Password
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Password", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = inputIconColor) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = brightGreen,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,   // ✅ Garis bawah dihilangkan karena item terakhir
                                unfocusedIndicatorColor = Color.Transparent  // ✅ Garis bawah dihilangkan
                            ),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Register
                Button(
                    onClick = { viewModel.register(username, email, password) },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brightGreen),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(text = "Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Status Message
                if (viewModel.registerStatus.isNotEmpty()) {
                    Text(
                        text = viewModel.registerStatus,
                        // ✅ Menggunakan brightGreen alih-alih warna gelap agar tetap terlihat di mode gelap
                        color = if (viewModel.registerStatus.contains("berhasil", ignoreCase = true)) brightGreen else Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sudah punya akun? ", color = Color.Gray)
                    TextButton(onClick = onNavigateToLogin) {
                        Text("Sign In", color = brightGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}