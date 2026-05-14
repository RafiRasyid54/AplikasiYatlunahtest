package com.yatlunah.app.data.model

import com.google.gson.annotations.SerializedName

data class GroupListResponse(
    @SerializedName("guru_id") val guru_id: String,
    @SerializedName("nama_guru") val nama_guru: String,
    @SerializedName("jumlah_santri") val jumlah_santri: Int
)

data class GroupDetailResponse(
    @SerializedName("nama_guru") val nama_guru: String,
    @SerializedName("students") val students: List<UserResponse>
)