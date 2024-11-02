package com.tehranmunicipality.assetmanagement.ui.asset_information

import com.tehranmunicipality.assetmanagement.data.model.GoodList

interface GoodListItemClickListener {
    fun itemClicked(goodListItem: GoodList)
}