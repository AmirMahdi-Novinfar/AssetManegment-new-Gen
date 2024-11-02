package com.tehranmunicipality.assetmanagement.ui.asset_information

import com.tehranmunicipality.assetmanagement.data.model.ArticlePatternListItem

interface ArticlePatternItemClickListener {
    fun itemClicked(articlePatternListItem: ArticlePatternListItem)
}