package com.yatlunah.app.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToRegister: () -> Unit,
    // ✅ Parameter 4 (role) sudah sesuai dengan MainActivity
    onLoginSuccess: (String, String, String, String, String?) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val brightGreen = Color(0xFF00D639)
    val inputIconColor = Color(0xFF2B2B43)

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        // --- AREA LOGO ---
        Box(modifier = Modifier.fillMaxWidth().weight(0.4f), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.logo_yatlunah),
                contentDescription = "Logo Yatlunah",
                modifier = Modifier.size(180.dp)
            )
        }

        // --- AREA FORM ---
        Surface(
            modifier = Modifier.fillMaxWidth().weight(0.6f)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LOGIN",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("Email", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = inputIconColor) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                // ✅ Menggunakan warna dinamis MaterialTheme agar aman di Dark/Light Mode
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = brightGreen, // 💡 Kursor diubah jadi hijau agar lebih cantik
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.LightGray,
                                unfocusedIndicatorColor = Color.LightGray
                            ),
                            singleLine = true
                        )

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
                                // ✅ Menghapus hardcode Color.Black agar tidak hilang di Dark Mode
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = brightGreen, // 💡 Kursor hijau
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent, // 💡 Baris bawah dihilangkan karena ini item terakhir di dalam Card
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    }
                }

                if (viewModel.loginStatus.isNotEmpty() && !viewModel.loginStatus.contains("Selamat")) {
                    Text(
                        text = viewModel.loginStatus,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = brightGreen)
                } else {
                    Button(
                        onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                // ✅ PERBAIKAN: Ubah idMitra menjadi mitra
                                viewModel.login(email, password) { id, nama, emailRes, role, mitra ->
                                    onLoginSuccess(id, nama, emailRes, role, mitra) // <-- Gunakan 'mitra' di sini
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = brightGreen),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                    Text("Belum punya akun? ", color = Color.Gray)
                    TextButton(onClick = onNavigateToRegister) {
                        Text("Sign Up", color = brightGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}