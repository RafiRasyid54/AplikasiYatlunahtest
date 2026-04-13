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
    onRegisterSucces: () -> Unit = {} // Pastikan typo 'Succes' ini sama dengan di MainActivity
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // ✅ LOGIKA NAVIGASI OTOMATIS
    // LaunchedEffect akan berjalan setiap kali viewModel.registerStatus berubah
    LaunchedEffect(viewModel.registerStatus) {
        if (viewModel.registerStatus.contains("berhasil", ignoreCase = true)) {
            // Kita panggil callback sukses yang akan memicu navigasi di MainActivity
            onRegisterSucces()
        }
    }

    val brightGreen = Color(0xFF00D639)
    val lightGrayBg = Color(0xFFF4F5F7)
    val inputIconColor = Color(0xFF2B2B43)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- Bagian Logo (Tetap sama) ---
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
            color = lightGrayBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SIGN UP",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // --- Form Input ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        TextField(
                            value = username,
                            onValueChange = { username = it },
                            placeholder = { Text("Username") },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = inputIconColor) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent),
                            singleLine = true
                        )
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("Email") },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = inputIconColor) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent),
                            singleLine = true
                        )
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = inputIconColor) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = null)
                                }
                            },
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent),
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
                    Text(text = "Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                // Status Message
                if (viewModel.registerStatus.isNotEmpty()) {
                    Text(
                        text = viewModel.registerStatus,
                        color = if (viewModel.registerStatus.contains("berhasil")) Color(0xFF2E7D32) else Color.Red,
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