package com.yatlunah.app.data.repository

import android.content.Context
import com.yatlunah.app.R
import com.yatlunah.app.data.model.JilidItem
import com.yatlunah.app.data.model.Materi

class MateriRepository {

    fun getDaftarJilid(): List<JilidItem> {
        return listOf(
            JilidItem(1, "Jilid 1", "Selesai", 1f, "pdf/jilid1.pdf"),
            JilidItem(2, "Jilid 2", "Sedang Berjalan", 0.3f, "pdf/jilid2.pdf"),
            JilidItem(3, "Jilid 3", "Belum Mulai", 0f, "pdf/jilid3.pdf"),
            JilidItem(4, "Jilid 4", "Belum Mulai", 0f, "pdf/jilid4.pdf")
        )
    }

    // ✅ Tambahkan parameter Context agar bisa mencari resource secara dinamis
    fun getMateriByHalaman(context: Context, jilidId: Int, halaman: Int): Materi {

        // 1. Buat pola nama file audio, misal: ji_hal01, ji_hal02, dst.
        val audioFileName = "ji_hal${String.format("%02d", halaman)}"

        // 2. Cari Resource ID berdasarkan nama file (0 jika tidak ketemu)
        val resId = context.resources.getIdentifier(audioFileName, "raw", context.packageName)

        return Materi(
            id = (jilidId * 100) + halaman,
            jilid = jilidId,
            halaman = halaman,
            teks = "Materi Jilid $jilidId Halaman $halaman",
            // 3. Jika resId ditemukan (bukan 0), gunakan resId tersebut. Jika tidak, beri null.
            audioResId = if (resId != 0) resId else null,
            isLatihan = false
        )
    }
    // Di dalam class MateriRepository
    fun saveProgress(userId: String, jilidId: Int, halaman: Int) {
        // Simulasi simpan ke Database (PostgreSQL/Supabase)
        println("Saving progress for user $userId: Jilid $jilidId, Halaman $halaman")
    }

    fun getLastProgress(userId: String, jilidId: Int): Int {
        // Simulasi ambil data dari Database, default balik ke halaman 1
        return 1
    }
}