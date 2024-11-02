package com.tehranmunicipality.assetmanagement.ui.base

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.GoodList
import com.tehranmunicipality.assetmanagement.ui.asset_information.GoodListItemClickListener
import com.tehranmunicipality.assetmanagement.util.englishToPersian
import java.util.*

class GoodListAdapter(
    private val itemClickListener: GoodListItemClickListener
) : RecyclerView.Adapter<GoodListAdapter.DataViewHolder>() {

    private var goodList: MutableList<GoodList> = mutableListOf()
    private var filteredGoodList: MutableList<GoodList> = mutableListOf()

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(_item: GoodList) {

            var item = "-"

            if (_item.productName != null) {
                Log.i("DEBUG","productName=${_item.productName}")
                item = "${_item.productName}"
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
        holder.bind(filteredGoodList[position])
        holder.itemView.findViewById<TextView>(R.id.tvItem).setOnClickListener {
            itemClickListener.itemClicked(filteredGoodList[position])
        }

        if (position == filteredGoodList.size - 1){
            holder.itemView.findViewById<TextView>(R.id.tvSeparator).visibility = View.GONE
        }else{
            holder.itemView.findViewById<TextView>(R.id.tvSeparator).visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return filteredGoodList.size
    }

    fun addData1(_goodListItem: List<GoodList>) {
        goodList = _goodListItem as MutableList<GoodList>
        notifyDataSetChanged()
    }

    fun addData(_goodListItem: GoodList) {
        goodList.add(_goodListItem)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        goodList.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(_goodItem: List<GoodList?>) {
        goodList = _goodItem as MutableList<GoodList>
        filteredGoodList = _goodItem as MutableList<GoodList>
        Log.i("DEBUG","GoodListAdapter addData=$_goodItem")
        notifyDataSetChanged()
    }

    fun search(searchText: String){
        filteredGoodList= goodList
        if (searchText.isEmpty()){
        }else{
            val resultList = mutableListOf<GoodList>()
            for (row in filteredGoodList){
                if (row.productName?.contains(searchText) == true){
                    resultList.add(row)
                }
            }
            filteredGoodList = resultList
            notifyDataSetChanged()
        }
    }

}



