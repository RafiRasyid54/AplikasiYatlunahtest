package com.yatlunah.app.data.remote

import com.google.gson.annotations.SerializedName
import com.yatlunah.app.data.model.JilidData
import com.yatlunah.app.data.model.Setoran
import retrofit2.Response
import retrofit2.http.*

interface MateriApiService {

    @GET("jilid/list")
    suspend fun getDaftarJilid(): List<JilidData>

    @POST("progress")
    suspend fun updateProgressToDatabase(
        @Body request: ProgressRequest
    ): ProgressResponse // ✅ Mengembalikan ProgressResponse

    @GET("audio/mapping/{jilid_id}/{page}")
    suspend fun getAudioUrl(
        @Path("jilid_id") jilidId: Int,
        @Path("page") halaman: Int
    ): AudioResponse

    @POST("setoran/tambah")
    suspend fun tambahSetoran(@Body request: SetoranRequest): Response<Unit>

    @GET("setoran/antrean/{jilid_id}")
    suspend fun getAntreanSetoran(
        @Path("jilid_id") jilidId: Int
    ): Response<List<Setoran>>
    @POST("setoran/nilai")
    suspend fun updateNilaiSetoran( @Body request: PenilaianRequest
    ): Response<Unit>

    @GET("users/{user_id}/riwayat")
    suspend fun getRiwayatSetoran(
        @Path("user_id") userId: String
    ): Response<List<Setoran>>
}


// --- DATA MODELS (Pastikan semua ini ada di bawah interface) ---

data class ProgressRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("jilid_id") val jilidId: Int,
    @SerializedName("halaman") val halaman: Int
)

data class ProgressResponse(
    val status: String // ✅ Ini yang dicari oleh Repository
)
data class SetoranRequest(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("jilid")
    val jilid: Int,

    @SerializedName("halaman")
    val halaman: Int,

    @SerializedName("audio_url")
    val audioUrl: String
)

data class AudioResponse(
    @SerializedName("audio_url") val audioUrl: String?,
    @SerializedName("judul_materi") val judulMateri: String?
)

// Update data class untuk kirim nilai
data class PenilaianRequest(
    @SerializedName("setoran_id") val setoranId: Int,
    @SerializedName("nilai") val nilai: Int,
    @SerializedName("catatan") val catatan: String,
    @SerializedName("id_guru_penilai") val idGuru: String // ✅ Tambahkan ini agar masuk ke DB
)