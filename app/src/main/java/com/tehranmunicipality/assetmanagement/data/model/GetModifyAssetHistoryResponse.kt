package com.tehranmunicipality.assetmanagement.data.model

import com.google.gson.annotations.SerializedName

class GetModifyAssetHistoryResponse(

    @field:SerializedName("result")
    val result: Int? = null,

    @field:SerializedName("detailError")
    val detailError: List<DetailErrorItem?>? = null
)
