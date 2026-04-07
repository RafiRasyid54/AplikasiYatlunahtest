package com.yatlunah.app.ui.screen.guru

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatlunah.app.data.model.Bimbingan // Sesuaikan import model kamu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruSantriAktifScreen(
    idGuru: String,
    onBack: () -> Unit,
    // Sementara pakai list dummy, nanti diganti dengan data dari ViewModel/API
    daftarSantri: List<Bimbingan> = listOf()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Santri Bimbingan Saya") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F5F7))
                .padding(padding)
                .padding(16.dp)
        ) {
            if (daftarSantri.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada santri bimbingan aktif.", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(daftarSantri) { santri ->
                        ItemSantriAktifCard(santri = santri)
                    }
                }
            }
        }
    }
}

@Composable
fun ItemSantriAktifCard(santri: Bimbingan) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "ID Santri: ${santri.userId}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Program: ${santri.jenisBimbingan}", color = Color.DarkGray, fontSize = 14.sp)
            Text(text = "Mulai: ${santri.tanggalDaftar}", color = Color.Gray, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Aksi
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = {
                        // Logika membuka WhatsApp
                        // Asumsi kita punya nomor telepon, tapi jika tidak, kirim ke nomor admin
                        val url = "https://api.whatsapp.com/send?phone=6281234567890&text=Assalamu'alaikum, ini Ustadz pembimbing dari aplikasi Yatlunah..."
                        val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)) // Warna khas WA
                ) {
                    Text("Hubungi Santri")
                }
            }
        }
    }
}