package com.yatlunah.app.data.manager

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.*
import java.io.File

interface SupabaseStorageApi {
    @Multipart
    @POST("storage/v1/object/{bucket}/{path}")
    suspend fun upload(
        @Header("Authorization") auth: String,
        @Header("apikey") apiKey: String,
        @Path("bucket") bucket: String,
        @Path("path") path: String,
        @Part file: MultipartBody.Part
    ): retrofit2.Response<Unit>
}

class AudioUploadManager {
    private val supabaseUrl = "https://ecueyemrcmaelxwqhbbo.supabase.co/" // GANTI INI
    private val apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVjdWV5ZW1yY21hZWx4d3FoYmJvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzMxNzMyNDMsImV4cCI6MjA4ODc0OTI0M30.9I1GbaCWGgLeUSuyyufyy85h1ASB4KYrpsVYDfhbEMs" // GANTI INI
    private val bucketName = "setoran_audio" // Pastikan nama bucket sama di dashboard

    private val client = OkHttpClient.Builder().build()
    private val api = Retrofit.Builder()
        .baseUrl(supabaseUrl)
        .client(client)
        .build()
        .create(SupabaseStorageApi::class.java)

    suspend fun uploadAudio(file: File, fileName: String): String? {
        return try {
            val requestBody = file.asRequestBody("audio/mpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", fileName, requestBody)

            val response = api.upload(
                auth = "Bearer $apiKey",
                apiKey = apiKey,
                bucket = bucketName,
                path = fileName,
                file = body
            )

            if (response.isSuccessful) {
                // Return URL Public
                "${supabaseUrl}storage/v1/object/public/$bucketName/$fileName"
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}