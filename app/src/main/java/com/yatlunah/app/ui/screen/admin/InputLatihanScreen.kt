package com.yatlunah.app.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yatlunah.app.data.model.LatihanSoal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputLatihanScreen(
    onBack: () -> Unit,
    onSave: (LatihanSoal) -> Unit
) {
    var jilid by remember { mutableStateOf("1") }
    var halaman by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var pertanyaan by remember { mutableStateOf("") }
    var jawaban by remember { mutableStateOf("") }

    // Tambahan state untuk pilihan ganda
    var pilihanA by remember { mutableStateOf("") }
    var pilihanB by remember { mutableStateOf("") }
    var pilihanC by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Input & Mapping Soal") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = kategori, onValueChange = { kategori = it }, label = { Text("Kategori (Contoh: AQSAL HALQ)") }, modifier = Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = jilid, onValueChange = { jilid = it }, label = { Text("Jilid") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = halaman, onValueChange = { halaman = it }, label = { Text("Halaman Target") }, modifier = Modifier.weight(1f))
            }

            OutlinedTextField(value = pertanyaan, onValueChange = { pertanyaan = it }, label = { Text("Pertanyaan") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Pilihan Ganda", style = MaterialTheme.typography.titleMedium)

            // Input Pilihan Jawaban
            OutlinedTextField(value = pilihanA, onValueChange = { pilihanA = it }, label = { Text("Pilihan 1") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = pilihanB, onValueChange = { pilihanB = it }, label = { Text("Pilihan 2") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = pilihanC, onValueChange = { pilihanC = it }, label = { Text("Pilihan 3") }, modifier = Modifier.fillMaxWidth())

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Info tambahan untuk meminimalisir typo kunci jawaban
            Text(
                text = "Pastikan Kunci Jawaban diketik SAMA PERSIS dengan salah satu pilihan di atas.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
            OutlinedTextField(value = jawaban, onValueChange = { jawaban = it }, label = { Text("Kunci Jawaban") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

            Button(
                onClick = {
                    // Menggabungkan pilihan yang diisi menjadi List
                    val pilihanList = listOf(pilihanA, pilihanB, pilihanC).filter { it.isNotBlank() }

                    onSave(LatihanSoal(
                        jilidId = jilid.toIntOrNull() ?: 1,
                        halamanTarget = halaman.toIntOrNull() ?: 0,
                        kategori = kategori,
                        pertanyaan = pertanyaan,
                        pilihanJawaban = pilihanList, // Dikirim sebagai List<String> ke API
                        kunciJawaban = jawaban
                    ))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Simpan & Mapping ke Halaman")
            }
        }
    }
}