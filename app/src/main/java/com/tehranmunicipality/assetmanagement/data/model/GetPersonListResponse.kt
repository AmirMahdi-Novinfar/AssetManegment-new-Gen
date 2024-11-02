package com.tehranmunicipality.assetmanagement.data.model

import com.google.gson.annotations.SerializedName

data class GetPersonListResponse(

    @field:SerializedName("personList")
    val personList: List<PersonListItem?>? = null,

    @field:SerializedName("detailError")
    val detailError: List<DetailErrorItem?>? = null
)

data class PersonListItem(

    @field:SerializedName("identityNo")
    val identityNo: String? = null,

    @field:SerializedName("firstName")
    var firstName: String? = null,

    @field:SerializedName("lastName")
    val lastName: String? = null,

    @field:SerializedName("actorId")
    var actorId: Int? = null,

    @field:SerializedName("costCenterId")
    var costCenterId: Int? = null,

    @field:SerializedName("costCenterName")
    var costCenterName: String? = null,

    @field:SerializedName("subCostCenterId")
    var subCostCenterId: Int? = null,

    @field:SerializedName("subCostCenterName")
    var subCostCenterName: String? = null
)
