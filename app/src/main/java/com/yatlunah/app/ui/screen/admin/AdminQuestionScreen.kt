package com.yatlunah.app.ui.screen.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yatlunah.app.data.model.LatihanSoal
import com.yatlunah.app.data.remote.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminQuestionScreen(onBack: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Palette Warna
    val brandGreen = Color(0xFF00D639)
    val bgColor = if (isDark) Color(0xFF0F0F0F) else Color(0xFFF8F9FA)
    val surfaceColor = if (isDark) Color(0xFF1A1A1A) else Color.White
    val textColor = if (isDark) Color.White else Color.Black

    var listSoal by remember { mutableStateOf<List<LatihanSoal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // State untuk kontrol mode: List atau Input (Tambah/Edit)
    var isInputMode by remember { mutableStateOf(false) }
    var selectedSoal by remember { mutableStateOf<LatihanSoal?>(null) }

    fun refreshData() {
        scope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.latihanApi.getAllSoal()
                if (response.isSuccessful) listSoal = response.body() ?: emptyList()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { refreshData() }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isInputMode) "Form Soal Latihan" else "Kelola Latihan Soal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isInputMode) isInputMode = false else onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = surfaceColor)
            )
        },
        floatingActionButton = {
            if (!isInputMode) {
                FloatingActionButton(
                    onClick = {
                        selectedSoal = null
                        isInputMode = true
                    },
                    containerColor = brandGreen,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah")
                }
            }
        }
    ) { innerPadding ->

        if (isInputMode) {
            // TAMPILAN HALAMAN INPUT
            QuestionInputLayout(
                modifier = Modifier.padding(innerPadding),
                soal = selectedSoal,
                isLoading = isLoading,
                brandGreen = brandGreen,
                onSave = { soalBaru ->
                    scope.launch {
                        try {
                            val response = if (selectedSoal == null) {
                                RetrofitClient.latihanApi.tambahSoalLatihan(soalBaru)
                            } else {
                                // Pastikan Anda memiliki endpoint updateSoal di API Service
                                RetrofitClient.latihanApi.updateSoal(selectedSoal!!.id ?: 0, soalBaru)
                            }

                            if (response.isSuccessful) {
                                Toast.makeText(context, "Berhasil disimpan", Toast.LENGTH_SHORT).show()
                                isInputMode = false
                                refreshData()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        } else {
            // TAMPILAN HALAMAN LIST (MONITORING)
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = brandGreen)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listSoal) { soal ->
                        QuestionItemCard(
                            soal = soal,
                            brandGreen = brandGreen,
                            surfaceColor = surfaceColor,
                            textColor = textColor,
                            onEdit = {
                                selectedSoal = soal
                                isInputMode = true
                            },
                            onDelete = {
                                scope.launch {
                                    val res = RetrofitClient.latihanApi.deleteSoal(soal.id ?: 0)
                                    if (res.isSuccessful) {
                                        Toast.makeText(context, "Terhapus", Toast.LENGTH_SHORT).show()
                                        refreshData()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionInputLayout(
    modifier: Modifier,
    soal: LatihanSoal?,
    isLoading: Boolean,
    brandGreen: Color,
    onSave: (LatihanSoal) -> Unit
) {
    var jilid by remember { mutableStateOf(soal?.jilidId?.toString() ?: "1") }
    var halaman by remember { mutableStateOf(soal?.halamanTarget?.toString() ?: "") }
    var kategori by remember { mutableStateOf(soal?.kategori ?: "") }
    var pertanyaan by remember { mutableStateOf(soal?.pertanyaan ?: "") }

    var pilihanA by remember { mutableStateOf(soal?.pilihanJawaban?.getOrNull(0) ?: "") }
    var pilihanB by remember { mutableStateOf(soal?.pilihanJawaban?.getOrNull(1) ?: "") }
    var pilihanC by remember { mutableStateOf(soal?.pilihanJawaban?.getOrNull(2) ?: "") }

    val initialSelectedIndex = soal?.pilihanJawaban?.indexOf(soal.kunciJawaban) ?: 0
    var selectedAnswerIndex by remember { mutableIntStateOf(if (initialSelectedIndex != -1) initialSelectedIndex else 0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Detail Mapping Soal", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        OutlinedTextField(
            value = kategori, onValueChange = { kategori = it },
            label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = jilid, onValueChange = { jilid = it },
                label = { Text("Jilid") }, modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = halaman, onValueChange = { halaman = it },
                label = { Text("Halaman PDF") }, modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }

        OutlinedTextField(
            value = pertanyaan, onValueChange = { pertanyaan = it },
            label = { Text("Pertanyaan") }, modifier = Modifier.fillMaxWidth().height(100.dp),
            shape = RoundedCornerShape(12.dp)
        )

        HorizontalDivider(thickness = 0.5.dp)
        Text("Pilihan Jawaban (Tandai yang Benar)", style = MaterialTheme.typography.titleSmall)

        // Opsi A
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selectedAnswerIndex == 0, onClick = { selectedAnswerIndex = 0 })
            OutlinedTextField(value = pilihanA, onValueChange = { pilihanA = it }, label = { Text("Pilihan A") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
        }
        // Opsi B
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selectedAnswerIndex == 1, onClick = { selectedAnswerIndex = 1 })
            OutlinedTextField(value = pilihanB, onValueChange = { pilihanB = it }, label = { Text("Pilihan B") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
        }
        // Opsi C
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selectedAnswerIndex == 2, onClick = { selectedAnswerIndex = 2 })
            OutlinedTextField(value = pilihanC, onValueChange = { pilihanC = it }, label = { Text("Pilihan C") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val listPilihan = listOf(pilihanA, pilihanB, pilihanC)
                onSave(LatihanSoal(
                    jilidId = jilid.toIntOrNull() ?: 1,
                    halamanTarget = halaman.toIntOrNull() ?: 0,
                    kategori = kategori,
                    pertanyaan = pertanyaan,
                    pilihanJawaban = listPilihan.filter { it.isNotBlank() },
                    kunciJawaban = listPilihan.getOrNull(selectedAnswerIndex) ?: ""
                ))
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
            enabled = pertanyaan.isNotBlank() && pilihanA.isNotBlank() && !isLoading
        ) {
            Text("Simpan Soal", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuestionItemCard(
    soal: LatihanSoal,
    brandGreen: Color,
    surfaceColor: Color,
    textColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Label Jilid & Hal
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(color = brandGreen.copy(0.1f), shape = CircleShape) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Book, null, tint = brandGreen, modifier = Modifier.size(12.dp))
                        Text(" Jilid ${soal.jilidId}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = brandGreen)
                    }
                }
                Surface(color = Color.Gray.copy(0.1f), shape = CircleShape) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Layers, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                        Text(" Hal ${soal.halamanTarget}", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = soal.pertanyaan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Surface(
                color = brandGreen.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
            ) {
                Text(
                    text = "Kunci: ${soal.kunciJawaban}",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 12.sp,
                    color = brandGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onEdit, colors = ButtonDefaults.textButtonColors(contentColor = brandGreen)) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp)
                }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onDelete, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red.copy(alpha = 0.7f))) {
                    Icon(Icons.Default.DeleteOutline, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Hapus", fontSize = 12.sp)
                }
            }
        }
    }
}