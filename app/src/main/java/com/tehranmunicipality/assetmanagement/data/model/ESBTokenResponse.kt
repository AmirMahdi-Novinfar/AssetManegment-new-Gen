package com.tehranmunicipality.assetmanagement.data.model

import com.google.gson.annotations.SerializedName

data class ESBTokenResponse(

    @field:SerializedName("access_token")
    val accessToken: String? = "",

    @field:SerializedName("refresh_token")
    val refreshToken: String? = "",

    @field:SerializedName("scope")
    val scope: String? = "",

    @field:SerializedName("token_type")
    val tokenType: String? = "",

    @field:SerializedName("expires_in")
    val expiresIn: Int? = 0,

    @field:SerializedName("error")
    val error: String? = "",

    @field:SerializedName("error_description")
    val error_description: String? = ""
)
