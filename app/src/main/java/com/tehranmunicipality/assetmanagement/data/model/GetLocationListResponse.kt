package com.tehranmunicipality.assetmanagement.data.model

import com.google.gson.annotations.SerializedName

data class GetLocationListResponse(

	@field:SerializedName("locationList")
	val locationList: List<LocationListItem?>? = null,

	@field:SerializedName("detailError")
	val detailError: List<DetailErrorItem?>? = null
)

data class LocationListItem(

	@field:SerializedName("assetLocationID")
	val assetLocationID: Int? = -1,

	@field:SerializedName("parentAssetLocationID")
	val parentAssetLocationID: Int? = null,

	@field:SerializedName("assetLocationName")
	val assetLocationName: String? = null,

	@field:SerializedName("levelNode")
	val levelNode: Int? = null
)
