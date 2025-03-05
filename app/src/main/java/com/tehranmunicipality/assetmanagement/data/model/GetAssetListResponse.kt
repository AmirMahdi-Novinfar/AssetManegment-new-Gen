package com.tehranmunicipality.assetmanagement.data.model

import com.google.gson.annotations.SerializedName

data class GetAssetListResponse(

    @field:SerializedName("detailError")
    val detailError: List<DetailErrorItem?>? = null,

    @field:SerializedName("assetList")
    val assetList: List<AssetListItem?>? = null
)

data class AssetListItem(

    @field:SerializedName("articlePatternCode")
    val articlePatternCode: Int? = null,

    @field:SerializedName("assetHistoryId")
    val assetHistoryId: Int? = null,

    @field:SerializedName("costCenterCode")
    val costCenterCode: String? = null,

    @field:SerializedName("assetStatusName")
    val assetStatusName: String? = null,

    @field:SerializedName("assetStateCode")
    val assetStateCode: Int? = null,

    @field:SerializedName("productID")
    val productID: Int? = null,

    @field:SerializedName("assetTypeName")
    val assetTypeName: String? = null,

    @field:SerializedName("regionName")
    val regionName: String? = null,

    @field:SerializedName("actorName")
    val actorName: String? = null,

    @field:SerializedName("assetTag")
    val assetTag: String? = null,

    @field:SerializedName("productName")
    val productName: String? = null,

    @field:SerializedName("identityNo")
    val identityNo: String? = null,

    @field:SerializedName("regionCode")
    val regionCode: Int? = null,

    @field:SerializedName("actorID")
    val actorID: Int? = null,

    @field:SerializedName("assetLocationName")
    val assetLocationName: String? = null,

    @field:SerializedName("assetHistoryDate")
    val assetHistoryDate: String? = null,

    @field:SerializedName("loginName")
    val loginName: String? = null,

    @field:SerializedName("subCostCenterName")
    val subCostCenterName: String? = null,

    @field:SerializedName("subCostCenterCode")
    val subCostCenterCode: String? = null,

    @field:SerializedName("assetCode")
    val assetCode: Int? = null,

    @field:SerializedName("assetStateName")
    val assetStateName: String? = null,

    @field:SerializedName("refrenceNo")
    val refrenceNo: String? = null,

    @field:SerializedName("assetStatusCode")
    val assetStatusCode: Int? = null,

    @field:SerializedName("articlePatternId")
    val articlePatternId: Int? = null,

    @field:SerializedName("articlePatternName")
    val articlePatternName: String? = null,

    @field:SerializedName("barCode")
    var barCode: String? = null,

    @field:SerializedName("assetLocationID")
    val assetLocationID: Int? = null,

    @field:SerializedName("subCostCenterID")
    val subCostCenterID: Int? = null,

    @field:SerializedName("costCenterID")
    val costCenterID: Int? = null,

    @field:SerializedName("assetID")
    val assetID: Int? = null,

    @field:SerializedName("costCenterName")
    val costCenterName: String? = null,

    @field:SerializedName("assetTypeCode")
    val assetTypeCode: Int? = null,

    @field:SerializedName("goodsCode")
    val goodsCode: Int? = null
)
