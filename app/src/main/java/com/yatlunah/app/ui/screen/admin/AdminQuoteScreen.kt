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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatQuote
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.data.model.QuotesHarian

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminQuoteScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    // Palette Warna
    val brandGreen = Color(0xFF00D639)
    val bgColor = if (isDark) Color(0xFF0F0F0F) else Color(0xFFF8F9FA)
    val surfaceColor = if (isDark) Color(0xFF1A1A1A) else Color.White

    val quotes by viewModel.quotes.collectAsState(initial = emptyList())
    val isLoading = viewModel.isLoading
    val isSuccess = viewModel.isQuoteSavedSuccess

    // State untuk kontrol mode: List (Monitoring) atau Input (Tambah/Edit)
    var isInputMode by remember { mutableStateOf(false) }
    var selectedQuote by remember { mutableStateOf<QuotesHarian?>(null) }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            isInputMode = false
            selectedQuote = null
            viewModel.resetQuoteStatus()
            Toast.makeText(context, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isInputMode) "Form Quote" else "Kelola Quote",
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = surfaceColor
                )
            )
        },
        floatingActionButton = {
            if (!isInputMode) {
                FloatingActionButton(
                    onClick = {
                        selectedQuote = null
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
            // TAMPILAN HALAMAN INPUT (BUKAN DIALOG)
            QuoteInputLayout(
                modifier = Modifier.padding(innerPadding),
                quote = selectedQuote,
                isLoading = isLoading,
                brandGreen = brandGreen,
                onSave = { teks, sumber, hari ->
                    if (selectedQuote == null) viewModel.saveQuote(teks, sumber, hari)
                    else viewModel.updateQuote(selectedQuote!!.id, teks, sumber, hari)
                }
            )
        } else {
            // TAMPILAN HALAMAN LIST (MONITORING)
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(quotes) { item ->
                    QuoteItemCard(
                        quote = item,
                        brandGreen = brandGreen,
                        surfaceColor = surfaceColor,
                        onEdit = {
                            selectedQuote = item
                            isInputMode = true
                        },
                        onDelete = { viewModel.deleteQuote(item.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteInputLayout(
    modifier: Modifier,
    quote: QuotesHarian?,
    isLoading: Boolean,
    brandGreen: Color,
    onSave: (String, String, String) -> Unit
) {
    var textState by remember { mutableStateOf(quote?.teksQuote ?: "") }
    var sourceState by remember { mutableStateOf(quote?.sumber ?: "") }
    var selectedHari by remember { mutableStateOf(quote?.hari ?: "Senin") }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Detail Quote", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedHari,
                onValueChange = {},
                readOnly = true,
                label = { Text("Jadwal Tampil Hari") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu").forEach { hari ->
                    DropdownMenuItem(
                        text = { Text(hari) },
                        onClick = { selectedHari = hari; expanded = false }
                    )
                }
            }
        }

        OutlinedTextField(
            value = textState,
            onValueChange = { textState = it },
            label = { Text("Pesan Inspiratif") },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        )

        OutlinedTextField(
            value = sourceState,
            onValueChange = { sourceState = it },
            label = { Text("Sumber / Tokoh") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { onSave(textState, sourceState, selectedHari) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
            enabled = textState.isNotBlank() && !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(Modifier.size(24.dp), color = Color.White)
            else Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuoteItemCard(
    quote: QuotesHarian,
    brandGreen: Color,
    surfaceColor: Color,
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
            // 1. Bagian Atas: Label Hari
            Surface(
                color = brandGreen.copy(0.1f),
                shape = CircleShape
            ) {
                Text(
                    quote.hari?.uppercase() ?: "UMUM",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = brandGreen
                )
            }

            Spacer(Modifier.height(12.dp))

            // 2. Bagian Tengah: Teks Quote
            Row {
                Icon(
                    Icons.Default.FormatQuote,
                    null,
                    tint = brandGreen.copy(0.2f),
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = quote.teksQuote,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "- ${quote.sumber}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // 3. Bagian Bawah: Tombol Aksi (Edit & Delete)
            // Menggunakan Divider tipis untuk memisahkan konten dan tombol
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End, // Tombol rapat ke kanan
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onEdit,
                    colors = ButtonDefaults.textButtonColors(contentColor = brandGreen)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp)
                }

                Spacer(Modifier.width(8.dp))

                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red.copy(alpha = 0.7f))
                ) {
                    Icon(Icons.Default.DeleteOutline, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Hapus", fontSize = 12.sp)
                }
            }
        }
    }
}