package com.tehranmunicipality.assetmanagement.data.model

import com.google.gson.annotations.SerializedName

data class GetSubCostCenterListResponse(

    @field:SerializedName("subCostCenterList")
    val subCostCenterList: List<SubCostCenterListItem?>? = null,

    @field:SerializedName("detailError")
    val detailError: List<DetailErrorItem?>? = null
)

data class SubCostCenterListItem(

    @field:SerializedName("costCenterCode")
    val costCenterCode: String? = null,

    @field:SerializedName("subCostCenterCode")
    val subCostCenterCode: String? = null,

    @field:SerializedName("subCostCenterID")
    val subCostCenterID: Int? = null,

    @field:SerializedName("costCenterID")
    val costCenterID: Int? = null,

    @field:SerializedName("costCenterName")
    val costCenterName: String? = null,

    @field:SerializedName("subCostCenterName")
    val subCostCenterName: String? = null
)
