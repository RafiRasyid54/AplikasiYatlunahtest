package com.yatlunah.app.ui.screen.santri

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SantriBimbinganDetailScreen(
    status: String,
    namaGuru: String,
    onNavigateToFormDaftar: () -> Unit,
    onBack: () -> Unit
) {
    // Logika penentuan keadaan status
    val normalizedStatus = status.lowercase().trim()
    val isDiterima = normalizedStatus == "aktif" || normalizedStatus == "diterima"
    val isMenunggu = normalizedStatus == "menunggu"
    val isDitolak = normalizedStatus == "ditolak"
    val isBelumDaftar = normalizedStatus.isEmpty() || normalizedStatus == "belum daftar"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Bimbingan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F5F7))
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- 1. IKON DINAMIS ---
            val icon = when {
                isDiterima -> Icons.Default.CheckCircle
                isDitolak -> Icons.Default.Error
                else -> Icons.Default.Info
            }

            val iconColor = when {
                isDiterima -> Color(0xFF00D639)
                isDitolak -> Color(0xFFF44336)
                else -> Color(0xFFFF9800)
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = iconColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. TEKS STATUS ---
            Text("Status Bimbingan:", color = Color.Gray, fontSize = 14.sp)
            Text(
                text = if (isBelumDaftar) "BELUM TERDAFTAR" else status.uppercase(),
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = iconColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- 3. KONTEN BERDASARKAN STATUS ---
            if (isDiterima) {
                // TAMPILAN JIKA SUDAH PUNYA GURU
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Pembimbing Anda:", fontSize = 14.sp, color = Color.Gray)
                        Text(
                            text = "Ustadz/ah $namaGuru",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { /* Logika WhatsApp */ },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Chat WhatsApp Guru", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // TAMPILAN MENUNGGU / DITOLAK / BELUM DAFTAR
                Text(
                    text = when {
                        isMenunggu -> "Pendaftaran Anda sedang diproses. Mohon tunggu konfirmasi dari admin atau guru."
                        isDitolak -> "Pendaftaran Anda ditolak. Silakan periksa kembali data Anda atau hubungi admin."
                        else -> "Anda belum terdaftar dalam program bimbingan manapun saat ini."
                    },
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // ✅ TOMBOL AKSI: Muncul jika TIDAK sedang menunggu
                if (!isMenunggu) {
                    Button(
                        onClick = onNavigateToFormDaftar,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCC9))
                    ) {
                        Text(
                            text = if (isDitolak) "Coba Daftar Lagi" else "Daftar Bimbingan Sekarang",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    // Tombol non-aktif saat menunggu
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Mohon Tunggu Konfirmasi...")
                    }
                }
            }
        }
    }
}