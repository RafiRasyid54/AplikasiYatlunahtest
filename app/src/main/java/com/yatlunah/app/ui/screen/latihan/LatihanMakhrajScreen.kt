package com.yatlunah.app.ui.screen.latihan

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.R

@Composable
fun LatihanMakhrajScreen(
    navController: androidx.navigation.NavController,
    viewModel: LatihanViewModel = viewModel()
) {
    val state = viewModel.uiState.value
    val isDark = isSystemInDarkTheme()

    // Warna selaras dengan MenuBelajarScreen Yatlunah
    val brandGreen = Color(0xFF00D639)
    val bgColor = if (isDark) Color(0xFF0F0F0F) else Color(0xFFF4F5F7)
    val surfaceColor = if (isDark) Color(0xFF1A1A1A) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF111111)


    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(bgColor), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = brandGreen)
        }
        return
    }

    if (state.isSelesai) {
        HasilAkhirLatihan(
            skor = state.skorAkhir, // Diperbaiki: menggunakan skorAkhir
            bgColor = bgColor,
            textColor = textColor,
            brandGreen = brandGreen,
            onKembali = { navController.popBackStack() }
        )
    } else if (viewModel.daftarSoal.isNotEmpty()) {
        val soal = viewModel.daftarSoal[state.currentIndex]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = { (state.currentIndex + 1).toFloat() / viewModel.daftarSoal.size },
                modifier = Modifier.fillMaxWidth().height(8.dp).padding(bottom = 16.dp),
                color = brandGreen,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_guru_illustration),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = soal.pertanyaan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Diperbaiki: referensi pilihanJawaban dan kunciJawaban
            soal.pilihanJawaban.forEach { pilihan ->
                val isCorrect = pilihan == soal.kunciJawaban
                val isSelected = state.jawabanTerpilih == pilihan

                val cardBgColor by animateColorAsState(
                    targetValue = when {
                        state.jawabanTerpilih == null -> surfaceColor
                        isCorrect -> Color(0xFF81C784)
                        isSelected && !isCorrect -> Color(0xFFE57373)
                        else -> surfaceColor
                    }, label = "ColorAnimation"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { viewModel.pilihJawaban(pilihan) },
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    border = BorderStroke(1.dp, if (state.jawabanTerpilih == null) brandGreen.copy(alpha = 0.5f) else Color.Transparent),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = pilihan,
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = if (state.jawabanTerpilih != null && (isCorrect || isSelected)) Color.White else textColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    } else {
        Box(modifier = Modifier.fillMaxSize().background(bgColor), contentAlignment = Alignment.Center) {
            Text("Tidak ada soal tersedia.", color = Color.Gray)
        }
    }
}

@Composable
fun HasilAkhirLatihan(
    skor: Int,
    bgColor: Color,
    textColor: Color,
    brandGreen: Color,
    onKembali: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(bgColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = R.drawable.ic_peserta_illustration), contentDescription = null, modifier = Modifier.size(150.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Masya Allah, Hebat!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = brandGreen)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Skor Kamu: $skor", fontSize = 22.sp, color = textColor)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onKembali,
            modifier = Modifier.fillMaxWidth(0.6f),
            colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
        ) {
            Text("Selesai")
        }
    }
}