package com.yatlunah.app.ui.screen.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.* // Memastikan semua komponen Material3 terimpor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

// Gunakan anotasi ini jika muncul error pada TopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminQuoteScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val brightGreen = Color(0xFF00D639)

    val isLoading = viewModel.isLoading
    val isSuccess = viewModel.isQuoteSavedSuccess
    val errorMessage = viewModel.errorMessage

    var quoteText by remember { mutableStateOf("") }
    var quoteSource by remember { mutableStateOf("") }

    LaunchedEffect(isSuccess, errorMessage) {
        if (isSuccess) {
            Toast.makeText(context, "Quote berhasil disimpan!", Toast.LENGTH_SHORT).show()
            quoteText = ""
            quoteSource = ""
            viewModel.resetQuoteStatus()
        }
        errorMessage?.let {
            Toast.makeText(context, "Gagal: $it", Toast.LENGTH_LONG).show()
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Quote Harian", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = quoteText,
                onValueChange = { quoteText = it },
                label = { Text("Isi Quote") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = quoteSource,
                onValueChange = { quoteSource = it },
                label = { Text("Sumber (Contoh: Al-Baqarah: 1)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (quoteText.isNotBlank() && quoteSource.isNotBlank()) {
                        viewModel.saveQuote(quoteText, quoteSource)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = brightGreen),
                enabled = !isLoading && quoteText.isNotBlank() && quoteSource.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Simpan Quote Harian", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}