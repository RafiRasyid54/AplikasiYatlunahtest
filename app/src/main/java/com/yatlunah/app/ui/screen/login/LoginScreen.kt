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
    // ✅ PERBAIKAN 1: Tambahkan satu String lagi untuk 'role'
    onLoginSuccess: (String, String, String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val brightGreen = Color(0xFF00D639)
    val lightGrayBg = Color(0xFFF4F5F7)
    val inputIconColor = Color(0xFF2B2B43)

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White)
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
            color = lightGrayBg
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("SIGN IN", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        TextField(
                            value = email, onValueChange = { email = it },
                            placeholder = { Text("Email", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = inputIconColor) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.LightGray,
                                unfocusedIndicatorColor = Color.LightGray
                            ),
                            singleLine = true
                        )

                        TextField(
                            value = password, onValueChange = { password = it },
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
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    }
                }

                if (viewModel.loginStatus.isNotEmpty() && !viewModel.loginStatus.contains("Selamat")) {
                    Text(viewModel.loginStatus, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = brightGreen)
                } else {
                    Button(
                        onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                // ✅ PERBAIKAN 2: Sesuaikan pemanggilan ViewModel agar menyertakan 'role'
                                viewModel.login(email, password) { id, nama, emailRes, role ->
                                    onLoginSuccess(id, nama, emailRes, role)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = brightGreen),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("Sign In", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
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