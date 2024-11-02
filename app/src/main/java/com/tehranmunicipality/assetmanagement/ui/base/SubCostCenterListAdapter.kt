package com.tehranmunicipality.assetmanagement.ui.base

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.SubCostCenterListItem
import com.tehranmunicipality.assetmanagement.ui.asset_information.ItemClickListener
import com.tehranmunicipality.assetmanagement.util.englishToPersian
import com.tehranmunicipality.assetmanagement.util.normalizeText
import java.util.*

class SubCostCenterListAdapter(
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<SubCostCenterListAdapter.DataViewHolder>() {

    private var subCostCenterList: MutableList<SubCostCenterListItem> = mutableListOf()
    private var filteredSubCostCenterList: MutableList<SubCostCenterListItem> = mutableListOf()

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(_item: SubCostCenterListItem) {

            var item = "-"

            if (_item.subCostCenterName != null) {
                Log.i("DEBUG","subCostCenterName=${_item.subCostCenterName}")
                item = "${_item.subCostCenterName}"
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
        holder.bind(filteredSubCostCenterList[position])
        holder.itemView.findViewById<TextView>(R.id.tvItem).setOnClickListener {
            itemClickListener.subCostCenterItemClicked(filteredSubCostCenterList[position])
        }

        if (position == filteredSubCostCenterList.size - 1){
            holder.itemView.findViewById<TextView>(R.id.tvSeparator).visibility = View.GONE
        }else{
            holder.itemView.findViewById<TextView>(R.id.tvSeparator).visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return filteredSubCostCenterList.size
    }

    fun addData1(_subCostCenterListItem: List<SubCostCenterListItem>) {
        subCostCenterList = _subCostCenterListItem as MutableList<SubCostCenterListItem>
        notifyDataSetChanged()
    }

    fun addData(_subCostCenterListItem: SubCostCenterListItem) {
        subCostCenterList.add(_subCostCenterListItem)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        subCostCenterList.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(_subCostCenterList: List<SubCostCenterListItem?>) {
        subCostCenterList = _subCostCenterList as MutableList<SubCostCenterListItem>
        filteredSubCostCenterList = _subCostCenterList
        Log.i("DEBUG","SubCostCenterListAdapter addData=$subCostCenterList")
        notifyDataSetChanged()
    }

    fun search(searchText: String){
        filteredSubCostCenterList = subCostCenterList
        if (searchText.isEmpty()){
        }else{
            val resultList = mutableListOf<SubCostCenterListItem>()
            for (row in filteredSubCostCenterList){
                row.costCenterName
                if (row.costCenterName?.normalizeText()?.contains(searchText,true) == true){
                    resultList.add(row)
                }
            }
            filteredSubCostCenterList = resultList
            notifyDataSetChanged()
        }
    }

}



