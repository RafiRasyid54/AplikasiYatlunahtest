package com.yatlunah.app.data.remote

import com.google.gson.annotations.SerializedName
import com.yatlunah.app.data.model.JilidData
import com.yatlunah.app.data.model.QuotesHarian
import com.yatlunah.app.data.model.Setoran
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MateriApiService {

    @GET("jilid/list")
    suspend fun getDaftarJilid(): List<JilidData>

    @POST("progress")
    suspend fun updateProgressToDatabase(
        @Body request: ProgressRequest
    ): ProgressResponse

    @GET("audio/mapping/{jilid_id}/{page}")
    suspend fun getAudioUrl(
        @Path("jilid_id") jilidId: Int,
        @Path("page") halaman: Int
    ): AudioResponse

    // --- FITUR SETORAN ---

    // Fungsi untuk mengunggah rekaman audio (Multipart) agar tersimpan ke database
    @Multipart
    @POST("setoran/upload")
    suspend fun uploadSetoran(
        @Part file: MultipartBody.Part,
        @Part("user_id") userId: RequestBody,
        @Part("jilid") jilid: RequestBody,
        @Part("halaman") halaman: RequestBody
    ): Response<ResponseBody>

    @POST("setoran/tambah")
    suspend fun tambahSetoran(@Body request: SetoranRequest): Response<Unit>

    @GET("setoran/antrean/{jilid_id}")
    suspend fun getAntreanSetoran(
        @Path("jilid_id") jilidId: Int
    ): Response<List<Setoran>>

    @POST("setoran/nilai")
    suspend fun updateNilaiSetoran(
        @Body request: PenilaianRequest
    ): Response<Unit>

    @GET("users/{user_id}/riwayat")
    suspend fun getRiwayatSetoran(
        @Path("user_id") userId: String
    ): Response<List<Setoran>>

    // --- MANAJEMEN QUOTES ---

    @POST("admin/quotes")
    suspend fun tambahQuote(@Body quote: QuotesHarian): Response<Unit>

    @GET("admin/quotes")
    suspend fun getAllQuotes(): Response<List<QuotesHarian>>

    @GET("admin/quotes/filter")
    suspend fun getQuotesByHari(
        @Query("hari") hari: String
    ): Response<QuotesHarian>

    @PUT("admin/quotes/{id}")
    suspend fun updateQuote(
        @Path("id") id: Int,
        @Body quote: QuotesHarian
    ): Response<Unit>

    @DELETE("admin/quotes/{id}")
    suspend fun deleteQuote(@Path("id") id: Int): Response<Unit>
}


// --- DATA MODELS ---

data class ProgressRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("jilid_id") val jilidId: Int,
    @SerializedName("halaman") val halaman: Int
)

data class ProgressResponse(
    val status: String
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

data class PenilaianRequest(
    @SerializedName("setoran_id") val setoranId: Int,
    @SerializedName("nilai") val nilai: Int,
    @SerializedName("catatan") val catatan: String,
    @SerializedName("id_guru_penilai") val idGuru: String
)