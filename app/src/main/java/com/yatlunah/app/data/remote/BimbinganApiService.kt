package com.yatlunah.app.data.remote

import com.yatlunah.app.data.model.BimbinganRequest
import com.yatlunah.app.data.model.ProgramYatlunah // Pastikan ini di-import
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BimbinganApiService {
    // Pastikan ini @POST, bukan @GET
    @POST("rest/v1/bimbingan")
    suspend fun postBimbingan(@Body request: BimbinganRequest): Response<Unit>
    @GET("rest/v1/program_yatlunah")
    suspend fun getPrograms(): List<ProgramYatlunah>
}