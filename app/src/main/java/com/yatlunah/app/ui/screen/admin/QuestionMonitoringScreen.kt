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