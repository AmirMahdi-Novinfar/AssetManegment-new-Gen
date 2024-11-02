package com.tehranmunicipality.assetmanagement.data.model

import com.google.gson.annotations.SerializedName

data class CostCenterList(
    @SerializedName("costCenterCode")
    var costCenterCode: String? = null,

    @SerializedName("costCenterID")
    var costCenterID: Int = 0,

    @SerializedName("costCenterName")
    var costCenterName: String? = null)
