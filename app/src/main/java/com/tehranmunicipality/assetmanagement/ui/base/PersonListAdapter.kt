package com.tehranmunicipality.assetmanagement.ui.base

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.PersonListItem
import com.tehranmunicipality.assetmanagement.ui.asset_information.ItemClickListener
import com.tehranmunicipality.assetmanagement.util.englishToPersian
import com.tehranmunicipality.assetmanagement.util.persianToEnglish
import java.util.*

class PersonListAdapter(
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<PersonListAdapter.DataViewHolder>() {

    private var personList: MutableList<PersonListItem> = mutableListOf()
    private var filteredPersonList: MutableList<PersonListItem> = mutableListOf()

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(_item: PersonListItem) {

            var item = "-"

            if (_item.firstName != null && _item.identityNo != null && _item.lastName != null) {
                Log.i("DEBUG","person=${_item.firstName}-${_item.lastName}-${_item.identityNo}")
                item = "${_item.identityNo} - ${_item.firstName} ${_item.lastName}"
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
        holder.bind(filteredPersonList[position])
        holder.itemView.findViewById<TextView>(R.id.tvItem).setOnClickListener {
            itemClickListener.personListItemClicked(filteredPersonList[position])
        }

        if (position == filteredPersonList.size - 1){
            holder.itemView.findViewById<TextView>(R.id.tvSeparator).visibility = View.GONE
        }else{
            holder.itemView.findViewById<TextView>(R.id.tvSeparator).visibility = View.VISIBLE
        }


    }

    override fun getItemCount(): Int {
        return filteredPersonList.size
    }

    fun addData1(_personList: List<PersonListItem>) {
        personList = _personList as MutableList<PersonListItem>
        notifyDataSetChanged()
    }

    fun addData(personListItem: PersonListItem) {
        personList.add(personListItem)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        personList.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(_personList: List<PersonListItem?>) {
        personList = _personList as MutableList<PersonListItem>
        filteredPersonList = _personList as MutableList<PersonListItem>
        Log.i("DEBUG","LocationListAdapter addData=$personList")
        notifyDataSetChanged()
    }

    fun search(searchText: String){
        filteredPersonList = personList
        if (searchText.isEmpty()){
        }else{
            val resultList = mutableListOf<PersonListItem>()
            for (row in filteredPersonList){
                if (row.firstName?.contains(persianToEnglish(searchText)) == true){
                    resultList.add(row)
                } else if (row.lastName?.contains(persianToEnglish(searchText)) == true){
                    resultList.add(row)
                }else if (row.identityNo?.contains(persianToEnglish(searchText)) == true){
                    resultList.add(row)
                }
            }
            filteredPersonList = resultList
            notifyDataSetChanged()
        }
    }

}



