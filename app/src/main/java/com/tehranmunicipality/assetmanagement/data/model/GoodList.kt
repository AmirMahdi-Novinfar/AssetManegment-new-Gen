package com.tehranmunicipality.assetmanagement.data.model

import com.google.gson.annotations.SerializedName

data class GoodList(
    @SerializedName("goodCode")
    var goodCode: String? = null,

    @SerializedName("productID")
    var productID: Int = 0,

    @SerializedName("productName")
    var productName: String? = null,

    @SerializedName("scaleName")
    var scaleName: String? = null)