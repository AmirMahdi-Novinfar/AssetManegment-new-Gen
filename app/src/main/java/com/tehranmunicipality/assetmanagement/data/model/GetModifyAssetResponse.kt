package com.tehranmunicipality.assetmanagement.data.model

import com.google.gson.annotations.SerializedName

data class GetModifyAssetResponse(

    @field:SerializedName("result")
    val result: Int? = null,

    @field:SerializedName("detailError")
    val detailError: List<DetailErrorItem?>? = null
)

data class DetailErrorItem(

    @field:SerializedName("innerException")
    val innerException: String? = null,

    @field:SerializedName("errorDesc")
    val errorDesc: String? = null,

    @field:SerializedName("errorCode")
    val errorCode: Int? = null
)
