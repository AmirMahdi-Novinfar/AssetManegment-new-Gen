package com.tehranmunicipality.assetmanagement.ui.asset_information

import com.tehranmunicipality.assetmanagement.data.model.*

interface ItemClickListener {

    fun articlePatternItemClicked(articlePatternListItem: ArticlePatternListItem)

    fun goodListItemClicked(goodListItem: GoodList)

    fun locationItemClicked(locationListItem: LocationListItem)

    fun costCenterItemClicked(costCenterListItem: CostCenterListItem)

    fun subCostCenterItemClicked(subCostCenterListItem: SubCostCenterListItem)

    fun personListItemClicked(personListItem: PersonListItem)

    fun assetStatusItemClicked(assetStatusListItem: AssetStatusListItem)
}