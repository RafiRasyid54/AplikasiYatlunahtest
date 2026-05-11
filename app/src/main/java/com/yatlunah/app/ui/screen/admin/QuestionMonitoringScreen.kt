package com.yatlunah.app.ui.screen.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.data.model.LatihanSoal
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionMonitoringScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val brightGreen = Color(0xFF00D639) // Disamakan dengan Quotes

    val questions by viewModel.questions.collectAsState(initial = emptyList())
    val isLoading = viewModel.isLoading
    val isSuccess = viewModel.isActionSuccess
    val errorMessage = viewModel.errorMessage

    var showDialog by remember { mutableStateOf(false) }
    var selectedSoal by remember { mutableStateOf<LatihanSoal?>(null) }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            showDialog = false
            selectedSoal = null
            viewModel.resetStatus()
            Toast.makeText(context, "Berhasil memperbarui data", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Soal Latihan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedSoal = null
                    showDialog = true
                },
                containerColor = brightGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Soal")
            }
        }
    ) { innerPadding ->

        if (showDialog) {
            SoalFormDialog(
                soal = selectedSoal,
                isLoading = isLoading,
                onDismiss = { showDialog = false },
                onConfirm = { dataBaru ->
                    if (selectedSoal == null) {
                        viewModel.saveSoal(dataBaru)
                    } else {
                        viewModel.updateSoal(selectedSoal!!.id ?: 0, dataBaru)
                    }
                }
            )
        }

        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isLoading && questions.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = brightGreen)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(questions) { item ->
                        QuestionItem(
                            soal = item,
                            onEdit = {
                                selectedSoal = item
                                showDialog = true
                            },
                            onDelete = { viewModel.deleteSoal(item.id ?: 0) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionItem(soal: LatihanSoal, onEdit: () -> Unit, onDelete: () -> Unit) {
    val brightGreen = Color(0xFF00D639)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = brightGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Jilid ${soal.jilidId} - Hal ${soal.halamanTarget}",
                            fontSize = 10.sp,
                            color = brightGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = soal.pertanyaan, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = "Kunci: ${soal.kunciJawaban}",
                    fontSize = 12.sp,
                    color = brightGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Blue)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoalFormDialog(
    soal: LatihanSoal?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (LatihanSoal) -> Unit
) {
    var jilid by remember { mutableStateOf(soal?.jilidId?.toString() ?: "1") }
    var halaman by remember { mutableStateOf(soal?.halamanTarget?.toString() ?: "") }
    var pertanyaan by remember { mutableStateOf(soal?.pertanyaan ?: "") }
    var kategori by remember { mutableStateOf(soal?.kategori ?: "Makhraj") }

    val options = remember {
        mutableStateListOf<String>().apply {
            if (soal != null && soal.pilihanJawaban.size >= 3) {
                addAll(soal.pilihanJawaban.take(3))
            } else {
                addAll(listOf("", "", ""))
            }
        }
    }

    var correctIndex by remember {
        mutableIntStateOf(
            if (soal != null) options.indexOf(soal.kunciJawaban).coerceAtLeast(0) else 0
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (soal == null) "Tambah Soal Baru" else "Edit Soal") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = jilid, onValueChange = { jilid = it },
                        label = { Text("Jilid") }, modifier = Modifier.weight(1f), enabled = !isLoading
                    )
                    OutlinedTextField(
                        value = halaman, onValueChange = { halaman = it },
                        label = { Text("Halaman") }, modifier = Modifier.weight(1f), enabled = !isLoading
                    )
                }

                OutlinedTextField(
                    value = pertanyaan, onValueChange = { pertanyaan = it },
                    label = { Text("Pertanyaan") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text("Pilihan Jawaban (Pilih yang benar):", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                options.forEachIndexed { index, text ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = (correctIndex == index),
                            onClick = { correctIndex = index },
                            enabled = !isLoading
                        )
                        OutlinedTextField(
                            value = text,
                            onValueChange = { options[index] = it },
                            label = { Text("Opsi ${index + 1}") },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading,
                            singleLine = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        LatihanSoal(
                            id = soal?.id,
                            jilidId = jilid.toIntOrNull() ?: 1,
                            halamanTarget = halaman.toIntOrNull() ?: 1,
                            pertanyaan = pertanyaan,
                            kategori = kategori,
                            pilihanJawaban = options.toList(),
                            kunciJawaban = options[correctIndex]
                        )
                    )
                },
                enabled = !isLoading && pertanyaan.isNotBlank() && options.all { it.isNotBlank() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D639))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Simpan")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Batal") }
        }
    )
}