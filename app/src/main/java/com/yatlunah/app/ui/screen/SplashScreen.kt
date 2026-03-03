package com.yatlunah.app.ui.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.yatlunah.app.R

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Timer 5 detik sebelum pindah ke Login
    LaunchedEffect(Unit) { // Menggunakan Unit lebih standar untuk key yang cuma sekali jalan
        delay(5000L)
        onTimeout()
    }

    // Background putih bersih agar logo menonjol
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_yatlunah),
            contentDescription = "Logo Yatlunah",
            modifier = Modifier.size(220.dp) // Sedikit lebih besar agar makin eye-catching
        )
    }
}