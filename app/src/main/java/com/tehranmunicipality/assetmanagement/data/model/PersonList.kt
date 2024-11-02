package com.tehranmunicipality.assetmanagement.data.model

import com.google.gson.annotations.SerializedName

data class PersonInfo(
    @SerializedName("actorId")
    var actorId: Int = 0,

    @SerializedName("firstName")
    var firstName: String? = null,

    @SerializedName("identityNo")
    var identityNo: String? = null,

    @SerializedName("lastName")
    var lastName: String? = null)