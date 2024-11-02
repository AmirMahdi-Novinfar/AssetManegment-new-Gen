package com.tehranmunicipality.assetmanagement.data.model

import com.google.gson.annotations.SerializedName

data class GetArticlePatternListResponse(

    @field:SerializedName("articlePatternList")
    val articlePatternList: List<ArticlePatternListItem?>? = null,

    @field:SerializedName("detailError")
    val detailError: List<DetailErrorItem?>? = null
)

data class ArticlePatternListItem(

    @field:SerializedName("articlePatternCode")
    val articlePatternCode: String? = null,

    @field:SerializedName("articlePatternID")
    val articlePatternID: Int? = null,

    @field:SerializedName("articlePatternName")
    val articlePatternName: String? = null
)
