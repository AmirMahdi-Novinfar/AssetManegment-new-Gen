package com.tehranmunicipality.assetmanagement.ui.user_information

import com.tehranmunicipality.assetmanagement.data.model.AssetListItem

interface ItemClickListener {
    fun itemClicked(assetListItem: AssetListItem)
}