package com.tehranmunicipality.assetmanagement.data.model

import com.google.gson.annotations.SerializedName

class GetGoodListResponse {
    @field:SerializedName("detailError")
    val detailError: List<DetailErrorItem?>? = null

    @SerializedName("goodList")
    var goodList: List<GoodList>? = null
}