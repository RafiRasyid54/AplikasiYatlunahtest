package com.yatlunah.app.ui.screen.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatlunah.app.data.model.LatihanSoal
import com.yatlunah.app.data.remote.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionMonitoringScreen(onBack: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val brandGreen = Color(0xFF00D639)
    val bgColor = if (isDark) Color(0xFF0F0F0F) else Color(0xFFF4F5F7)
    val surfaceColor = if (isDark) Color(0xFF1A1A1A) else Color.White
    val textColor = if (isDark) Color.White else Color.Black

    var listSoal by remember { mutableStateOf<List<LatihanSoal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun refreshData() {
        scope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.latihanApi.getAllSoal()
                if (response.isSuccessful) listSoal = response.body() ?: emptyList()
            } finally { isLoading = false }
        }
    }

    LaunchedEffect(Unit) { refreshData() }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Monitoring Pertanyaan", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = surfaceColor, titleContentColor = textColor)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = brandGreen)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(listSoal) { soal ->
                    QuestionItemCard(soal, surfaceColor, textColor, brandGreen, onDelete = {
                        scope.launch {
                            val res = RetrofitClient.latihanApi.deleteSoal(soal.id ?: 0)
                            if (res.isSuccessful) {
                                Toast.makeText(context, "Terhapus", Toast.LENGTH_SHORT).show()
                                refreshData()
                            }
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun QuestionItemCard(soal: LatihanSoal, surfaceColor: Color, textColor: Color, brandGreen: Color, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Book, null, tint = brandGreen, modifier = Modifier.size(14.dp))
                    Text(" Jilid ${soal.jilidId}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = brandGreen)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Layers, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Text(" Hal ${soal.halamanTarget}", fontSize = 12.sp, color = Color.Gray)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, null, tint = Color.Red.copy(alpha = 0.6f))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(text = soal.pertanyaan, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = textColor)
            Spacer(Modifier.height(8.dp))
            Surface(color = brandGreen.copy(alpha = 0.05f), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                Text(text = "Kunci: ${soal.kunciJawaban}", modifier = Modifier.padding(8.dp), fontSize = 12.sp, color = brandGreen)
            }
        }
    }
}