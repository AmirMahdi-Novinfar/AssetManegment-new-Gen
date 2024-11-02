package com.tehranmunicipality.assetmanagement.data.model

import com.google.gson.annotations.SerializedName

data class SubCostCenterList(
    @SerializedName("costCenterCode")
    var costCenterCode: String? = null,

    @SerializedName("costCenterID")
    var costCenterID: Int = 0,

    @SerializedName("costCenterName")
    var costCenterName: String? = null,

    @SerializedName("subCostCenterCode")
    var subCostCenterCode: String? = null,

    @SerializedName("subCostCenterID")
    var subCostCenterID: Int = 0,

    @SerializedName("subCostCenterName")
    var subCostCenterName: String? = null)