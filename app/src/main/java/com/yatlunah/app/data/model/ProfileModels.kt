package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class UserNameUpdate(val nama_lengkap: String)

// Di file model Android kamu
data class UserPasswordUpdate(
    @SerializedName("old_password") // ✅ Harus sama persis dengan key di JSON FastAPI
    val old_password: String,

    @SerializedName("new_password")
    val new_password: String
)