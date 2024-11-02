package com.tehranmunicipality.assetmanagement.ui.base

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.LocationListItem
import com.tehranmunicipality.assetmanagement.ui.asset_information.ItemClickListener
import com.tehranmunicipality.assetmanagement.util.englishToPersian
import java.util.*

class LocationListAdapter(
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<LocationListAdapter.DataViewHolder>() {

    private var locationList: MutableList<LocationListItem> = mutableListOf()
    private var filteredLocationList: MutableList<LocationListItem> = mutableListOf()

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(_item: LocationListItem) {

            var item = "-"

            if (_item.assetLocationName != null) {
                Log.i("DEBUG","assetLocationName=${_item.assetLocationName}")
                item = "${_item.assetLocationName}"
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
        holder.bind(filteredLocationList[position])
        holder.itemView.findViewById<TextView>(R.id.tvItem).setOnClickListener {
            itemClickListener.locationItemClicked(filteredLocationList[position])
        }

        if (position == filteredLocationList.size - 1){
            holder.itemView.findViewById<TextView>(R.id.tvSeparator).visibility = View.GONE
        }else{
            holder.itemView.findViewById<TextView>(R.id.tvSeparator).visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return filteredLocationList.size
    }

    fun addData1(_locationList: List<LocationListItem>) {
        locationList = _locationList as MutableList<LocationListItem>
        notifyDataSetChanged()
    }

    fun addData(locationItem: LocationListItem) {
        locationList.add(locationItem)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        locationList.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(_locationList: List<LocationListItem?>) {
        locationList = _locationList as MutableList<LocationListItem>
        filteredLocationList = _locationList as MutableList<LocationListItem>
        Log.i("DEBUG","LocationListAdapter addData=$locationList")
        notifyDataSetChanged()
    }

    fun search(searchText: String){
        filteredLocationList = locationList
        if (searchText.isEmpty()){
        }else{
            val resultList = mutableListOf<LocationListItem>()
            for (row in filteredLocationList){
                if (row.assetLocationName?.contains(searchText) == true){
                    resultList.add(row)
                }
            }
            filteredLocationList = resultList
            notifyDataSetChanged()
        }
    }
}



