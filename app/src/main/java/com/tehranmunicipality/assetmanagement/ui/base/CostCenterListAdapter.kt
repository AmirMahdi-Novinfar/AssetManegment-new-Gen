package com.tehranmunicipality.assetmanagement.ui.base

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.CostCenterListItem
import com.tehranmunicipality.assetmanagement.ui.asset_information.CostCenterItemClickListener
import com.tehranmunicipality.assetmanagement.util.englishToPersian
import java.util.*

class CostCenterListAdapter(
    private val itemClickListener: CostCenterItemClickListener
) : RecyclerView.Adapter<CostCenterListAdapter.DataViewHolder>() {

    private var costCenterList: MutableList<CostCenterListItem> = mutableListOf()
    private var filteredCostCenterList: MutableList<CostCenterListItem> = mutableListOf()

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(_item: CostCenterListItem) {

            var item = "-"

            if (_item.costCenterName != null) {
                Log.i("DEBUG", "costCenterName=${_item.costCenterName}")
                item = "${_item.costCenterName}"
                itemView.findViewById<TextView>(R.id.tvItem).text =
                    "${englishToPersian(item)}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_searchable_spinner, parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(filteredCostCenterList[position])
        holder.itemView.findViewById<TextView>(R.id.tvItem).setOnClickListener {
            itemClickListener.itemClicked(filteredCostCenterList[position])
        }
    }

    override fun getItemCount(): Int {
        return filteredCostCenterList.size
    }

    fun addData1(_costCenterListItem: List<CostCenterListItem>) {
        costCenterList = _costCenterListItem as MutableList<CostCenterListItem>
        notifyDataSetChanged()
    }

    fun addData(_costCenterListItem: CostCenterListItem) {
        costCenterList.add(_costCenterListItem)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        costCenterList.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(_costCenterList: List<CostCenterListItem?>) {
        costCenterList = _costCenterList as MutableList<CostCenterListItem>
        filteredCostCenterList = _costCenterList as MutableList<CostCenterListItem>
        Log.i("DEBUG", "CostCenterListAdapter addData=$costCenterList")
        notifyDataSetChanged()
    }

    fun search(searchText: String) {
        filteredCostCenterList = costCenterList
        if (searchText.isEmpty()) {
        } else {
            val resultList = mutableListOf<CostCenterListItem>()
            for (row in filteredCostCenterList) {
                if (row.costCenterName?.contains(searchText) == true) {
                    resultList.add(row)
                }
            }
            filteredCostCenterList = resultList
            notifyDataSetChanged()
        }
    }

}



