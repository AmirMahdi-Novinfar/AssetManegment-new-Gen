package com.tehranmunicipality.assetmanagement.ui.asset_information

import com.tehranmunicipality.assetmanagement.data.model.AssetListItem

interface AssetItemClickListener {
    fun editItemClicked(assetListItem: AssetListItem)
    fun setBarcodeClicked(assetListItem: AssetListItem)
     fun setAssetMoreClicked(assetListItem: AssetListItem)
}