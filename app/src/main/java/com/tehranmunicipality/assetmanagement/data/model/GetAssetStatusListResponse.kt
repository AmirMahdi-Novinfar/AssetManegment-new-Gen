package com.tehranmunicipality.assetmanagement.data.model

import com.google.gson.annotations.SerializedName

data class GetAssetStatusListResponse(

	@field:SerializedName("detailError")
	val detailError: List<DetailErrorItem?>? = null,

	@field:SerializedName("assetStatusList")
	val assetStatusList: List<AssetStatusListItem?>? = null
)

data class AssetStatusListItem(

	@field:SerializedName("assetStatusName")
	val assetStatusName: String? = null,

	@field:SerializedName("assetStatusCode")
	val assetStatusCode: Int? = null
)
