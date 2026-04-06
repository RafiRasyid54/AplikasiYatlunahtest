package com.yatlunah.app.ui.screen.info_program

import androidx.lifecycle.ViewModel
import com.yatlunah.app.data.model.ProgramYatlunah
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProgramViewModel : ViewModel() {
    private val _programs = MutableStateFlow<List<ProgramYatlunah>>(emptyList())
    val programs: StateFlow<List<ProgramYatlunah>> = _programs

    init {
        loadInitialPrograms()
    }

    private fun loadInitialPrograms() {
        // Di dalam init atau fungsi loadInitialPrograms
        _programs.value = listOf(
            ProgramYatlunah(
                id = 1,
                nama = "Program Reguler",
                deskripsi = "Pembelajaran rutin Jilid 1-6.",
                targetPeserta = "Anak & Remaja",
                materiUtama = "Jilid 1 sampai 6",
                fiturUnggulan = listOf("Audio eksklusif", "Monitoring harian"),
                iconRes = 0
            ),
            // ... ulangi format yang sama untuk program lainnya
        )
    }
}