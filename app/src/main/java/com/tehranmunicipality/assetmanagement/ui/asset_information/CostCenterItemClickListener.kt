package com.tehranmunicipality.assetmanagement.ui.asset_information

import com.tehranmunicipality.assetmanagement.data.model.CostCenterListItem

interface CostCenterItemClickListener {
    fun itemClicked(costCenterListItem: CostCenterListItem)
}