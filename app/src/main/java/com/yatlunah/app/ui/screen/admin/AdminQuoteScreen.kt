package com.yatlunah.app.ui.screen.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.yatlunah.app.data.model.QuotesHarian
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminQuoteScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val brightGreen = Color(0xFF00D639)

    val quotes by viewModel.quotes.collectAsState(initial = emptyList())
    val isLoading = viewModel.isLoading
    val isSuccess = viewModel.isQuoteSavedSuccess
    val errorMessage = viewModel.errorMessage

    // State untuk mengontrol kemunculan Dialog
    var showDialog by remember { mutableStateOf(false) }

    // State untuk menyimpan data yang sedang diedit (null jika tambah baru)
    var selectedQuote by remember { mutableStateOf<QuotesHarian?>(null) }

    // Reset dan tutup dialog jika sukses
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            showDialog = false
            selectedQuote = null
            viewModel.resetQuoteStatus()
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
                title = { Text("Kelola Quote Harian", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        },
        // Tombol melayang di pojok kanan bawah
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedQuote = null // Mode Tambah
                    showDialog = true
                },
                containerColor = brightGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Quote")
            }
        }
    ) { innerPadding ->

        // --- LOGIC POP-UP DIALOG ---
        if (showDialog) {
            QuoteFormDialog(
                quote = selectedQuote,
                isLoading = isLoading,
                onDismiss = { showDialog = false },
                onConfirm = { teks, sumber ->
                    if (selectedQuote == null) {
                        viewModel.saveQuote(teks, sumber)
                    } else {
                        viewModel.updateQuote(selectedQuote!!.id, teks, sumber)
                    }
                }
            )
        }

        // --- DAFTAR QUOTE ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quotes) { item ->
                QuoteItem(
                    quote = item,
                    onEdit = {
                        selectedQuote = item // Simpan data yang akan diedit
                        showDialog = true    // Munculkan pop-up
                    },
                    onDelete = { viewModel.deleteQuote(item.id) }
                )
            }
        }
    }
}

@Composable
fun QuoteFormDialog(
    quote: QuotesHarian?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    // State lokal di dalam dialog agar tidak mengganggu state utama
    var textState by remember { mutableStateOf(quote?.teksQuote ?: "") }
    var sourceState by remember { mutableStateOf(quote?.sumber ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (quote == null) "Tambah Quote Baru" else "Edit Quote")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = textState,
                    onValueChange = { textState = it },
                    label = { Text("Isi Quote") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = sourceState,
                    onValueChange = { sourceState = it },
                    label = { Text("Sumber") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(textState, sourceState) },
                enabled = !isLoading && textState.isNotBlank() && sourceState.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D639))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text("Simpan")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun QuoteItem(quote: QuotesHarian, onEdit: () -> Unit, onDelete: () -> Unit) {
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
                Text(text = "\"${quote.teksQuote}\"", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(text = "- ${quote.sumber}", fontSize = 12.sp, color = Color.Gray)
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