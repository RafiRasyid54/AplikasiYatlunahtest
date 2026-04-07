package com.yatlunah.app.data.remote

import com.yatlunah.app.data.model.BimbinganRequest
import com.yatlunah.app.data.model.ProgramYatlunah // Pastikan ini di-import
import com.yatlunah.app.data.model.BimbinganResponse
import com.yatlunah.app.data.model.UpdateStatusRequest
import com.yatlunah.app.data.model.UpdateStatusResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BimbinganApiService {
    // Pastikan ini @POST, bukan @GET
    @POST("rest/v1/bimbingan")
    suspend fun postBimbingan(@Body request: BimbinganRequest): Response<Unit>
    @GET("rest/v1/program_yatlunah")
    suspend fun getPrograms(): List<ProgramYatlunah>

    @GET("rest/v1/bimbingan/menunggu")
    suspend fun getBimbinganMenunggu(): Response<BimbinganResponse>

    @PUT("rest/v1/bimbingan/{id}")
    suspend fun updateStatusBimbingan(
        @Path("id") id: Int,
        @Body request: UpdateStatusRequest
    ): Response<UpdateStatusResponse>

    @GET("rest/v1/bimbingan/status/{user_id}")
    suspend fun getBimbinganStatusSantri(@Path("user_id") userId: String): Response<BimbinganResponse>
}