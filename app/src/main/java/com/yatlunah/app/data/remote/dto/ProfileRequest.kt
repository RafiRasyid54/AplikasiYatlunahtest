package com.yatlunah.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateNameRequest(
    @SerializedName("nama_lengkap") // Sesuaikan dengan field di FastAPI-mu
    val newName: String
)

data class UpdatePasswordRequest(
    @SerializedName("old_password") // Sesuaikan dengan FastAPI
    val oldPassword: String,

    @SerializedName("new_password") // Sesuaikan dengan FastAPI
    val newPassword: String
)