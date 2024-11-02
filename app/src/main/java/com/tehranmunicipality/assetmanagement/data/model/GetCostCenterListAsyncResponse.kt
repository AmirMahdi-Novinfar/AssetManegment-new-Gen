package com.tehranmunicipality.assetmanagement.data.model

import com.google.gson.annotations.SerializedName

data class GetCostCenterListAsyncResponse(

	@field:SerializedName("costCenterList")
	val costCenterList: List<CostCenterListItem?>? = null,

	@field:SerializedName("detailError")
	val detailError: List<DetailErrorItem?>? = null
)

data class CostCenterListItem(

	@field:SerializedName("costCenterCode")
	val costCenterCode: String? = null,

	@field:SerializedName("costCenterID")
	val costCenterID: Int? = null,

	@field:SerializedName("costCenterName")
	val costCenterName: String? = null
)
