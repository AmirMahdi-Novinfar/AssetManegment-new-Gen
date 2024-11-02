package com.tehranmunicipality.assetmanagement.ui.base

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.AssetStatusListItem
import com.tehranmunicipality.assetmanagement.ui.asset_information.ItemClickListener
import com.tehranmunicipality.assetmanagement.util.englishToPersian
import java.util.*

class AssetStatusListAdapter(
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<AssetStatusListAdapter.DataViewHolder>() {

    private var assetStatusList: MutableList<AssetStatusListItem> = mutableListOf()
    private var filteredAssetStatusList: MutableList<AssetStatusListItem> = mutableListOf()

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(_item: AssetStatusListItem) {

            var item = "-"

            if (_item.assetStatusName != null) {
                Log.i("DEBUG","assetStatusName=${_item.assetStatusName}")
                item = "${_item.assetStatusName}"
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
        holder.bind(filteredAssetStatusList[position])
        holder.itemView.findViewById<TextView>(R.id.tvItem).setOnClickListener {
            itemClickListener.assetStatusItemClicked(filteredAssetStatusList[position])
        }

        if (position == filteredAssetStatusList.size - 1){
            holder.itemView.findViewById<TextView>(R.id.tvSeparator).visibility = View.GONE
        }else{
            holder.itemView.findViewById<TextView>(R.id.tvSeparator).visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return filteredAssetStatusList.size
    }

    fun addData1(_assetStatusList: List<AssetStatusListItem>) {
        assetStatusList = _assetStatusList as MutableList<AssetStatusListItem>
        notifyDataSetChanged()
    }

    fun addData(_assetStatusListItem: AssetStatusListItem) {
        assetStatusList.add(_assetStatusListItem)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        assetStatusList.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(_assetStatusList: List<AssetStatusListItem>) {
        assetStatusList = _assetStatusList as MutableList<AssetStatusListItem>
                filteredAssetStatusList = _assetStatusList
        Log.i("DEBUG","LocationListAdapter addData=$assetStatusList")
        notifyDataSetChanged()
    }

    fun search(searchText: String){
        filteredAssetStatusList = assetStatusList
        if (searchText.isEmpty()){
        }else{
            val resultList = mutableListOf<AssetStatusListItem>()
            for (row in filteredAssetStatusList){
                if (row.assetStatusName?.contains(searchText) == true){
                    resultList.add(row)
                }
            }
            filteredAssetStatusList = resultList
            notifyDataSetChanged()
        }
    }

}



